@file:Suppress("PropertyName", "MemberVisibilityCanBePrivate")

package com.kgit2.repository

import cnames.structs.git_repository
import com.kgit2.annotations.Raw
import com.kgit2.apply.ApplyLocation
import com.kgit2.apply.ApplyOptions
import com.kgit2.blob.Blob
import com.kgit2.branch.Branch
import com.kgit2.branch.BranchIterator
import com.kgit2.branch.BranchType
import com.kgit2.checkout.CheckoutOptions
import com.kgit2.checkout.ResetType
import com.kgit2.cherrypick.CherrypickOptions
import com.kgit2.commit.AnnotatedCommit
import com.kgit2.commit.Commit
import com.kgit2.common.error.GitError
import com.kgit2.common.error.GitErrorCode
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.extend.toBoolean
import com.kgit2.common.extend.toInt
import com.kgit2.common.memory.Memory
import com.kgit2.common.memory.memoryScoped
import com.kgit2.config.Config
import com.kgit2.diff.Diff
import com.kgit2.index.Index
import com.kgit2.memory.GitBase
import com.kgit2.merge.MergeAnalysisFlag
import com.kgit2.merge.MergeOptions
import com.kgit2.merge.MergePreferenceFlag
import com.kgit2.model.toKString
import com.kgit2.model.toList
import com.kgit2.model.withGitBuf
import com.kgit2.model.withGitStrArray
import com.kgit2.`object`.Object
import com.kgit2.`object`.ObjectType
import com.kgit2.odb.Odb
import com.kgit2.oid.Oid
import com.kgit2.oid.OidArray
import com.kgit2.rebase.Rebase
import com.kgit2.rebase.RebaseOptions
import com.kgit2.reference.Reference
import com.kgit2.reference.ReferenceIterator
import com.kgit2.remote.Remote
import com.kgit2.revert.RevertOptions
import com.kgit2.signature.Signature
import com.kgit2.status.StatusFlag
import com.kgit2.status.StatusList
import com.kgit2.status.StatusOptions
import com.kgit2.submodule.Submodule
import com.kgit2.tree.Tree
import com.kgit2.worktree.Worktree
import kotlinx.cinterop.*
import libgit2.*

@Raw(
    base = git_repository::class,
    free = "git_repository_free",
)
class Repository(raw: RepositoryRaw) : GitBase<git_repository, RepositoryRaw>(raw) {
    constructor(memory: Memory, handler: RepositoryPointer) : this(RepositoryRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        secondary: RepositorySecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: RepositorySecondaryInitial? = null,
    ) : this(RepositoryRaw(memory, secondary, secondaryInitial))

    companion object {
        fun initial(path: String, bare: Boolean = false): Repository = Repository {
            git_repository_init(this.ptr, path, bare.toInt().convert()).errorCheck()
        }

        fun initialExt(path: String, options: RepositoryInitOptions): Repository = Repository {
            git_repository_init_ext(this.ptr, path, options.raw.handler).errorCheck()
        }

        fun open(path: String, bare: Boolean = false): Repository = Repository {
            when (bare) {
                true -> git_repository_open_bare(this.ptr, path)
                false -> git_repository_open(this.ptr, path)
            }.errorCheck()
        }

        fun openExt(
            path: String,
            flags: RepositoryOpenFlags = RepositoryOpenFlags.OpenFromENV,
            ceilingDirs: String? = null,
        ): Repository = Repository {
            git_repository_open_ext(this.ptr, path, flags.value, ceilingDirs).errorCheck()
        }

        fun openFromWorktree(worktree: Worktree): Repository = Repository {
            git_repository_open_from_worktree(this.ptr, worktree.raw.handler).errorCheck()
        }

        fun openFromEnv(): Repository = Repository {
            git_repository_open_ext(this.ptr, null, RepositoryOpenFlags.OpenFromENV.value, null).errorCheck()
        }

        fun discover(path: String): Repository {
            val discoverPath = withGitBuf { buf ->
                git_repository_discover(buf, path, 1, null).errorCheck()
                buf.toKString()!!
            }
            return open(discoverPath)
        }

        fun clone(url: String, localPath: String, cloneOptions: CloneOptions): Repository = Repository {
            git_clone(this.ptr, url, localPath, cloneOptions.raw.handler).errorCheck()
        }

        fun cloneRecursive(url: String, localPath: String, cloneOptions: CloneOptions): Repository {
            val repository = Repository {
                git_clone(this.ptr, url, localPath, cloneOptions.raw.handler).errorCheck()
            }
            repository.Submodule.updateSubmodules()
            return repository
        }

        fun fromOdb(odb: Odb): Repository = Repository {
            git_repository_wrap_odb(this.ptr, odb.raw.handler).errorCheck()
        }
    }

    val path: String?
        get() = git_repository_path(raw.handler)?.toKString()

    val isBare: Boolean
        get() = git_repository_is_bare(raw.handler).toBoolean()

    val isShallow: Boolean
        get() = git_repository_is_shallow(raw.handler).toBoolean()

    val isWorkTree: Boolean
        get() = git_repository_is_worktree(raw.handler).toBoolean()

    val isEmpty: Boolean
        get() = git_repository_is_empty(raw.handler).toBoolean()

    val noteDefaultRef: String
        get() = withGitBuf { buf ->
            git_note_default_ref(buf, raw.handler).errorCheck()
            buf.toKString()!!
        }

    val state: RepositoryState
        get() = RepositoryState.fromInt(git_repository_state(raw.handler).convert())

    val workDir: String?
        get() = git_repository_workdir(raw.handler)?.toKString()

    fun setWorkDir(path: String, updateGitLink: Boolean = false) {
        git_repository_set_workdir(raw.handler, path, updateGitLink.toInt()).errorCheck()
    }

    var namespace: String? = git_repository_get_namespace(raw.handler)?.toKString()
        set(value) {
            git_repository_set_namespace(raw.handler, value).errorCheck()
            field = value
        }

    fun removeNamespace() {
        git_repository_set_namespace(raw.handler, null).errorCheck()
    }

    val message: String? = withGitBuf { buf ->
        runCatching {
            git_repository_message(buf, raw.handler).errorCheck()
        }.onFailure {
            if (it is GitError && it.code == GitErrorCode.NotFound) return@withGitBuf null
        }.getOrThrow()
        buf.toKString()
    }

    fun removeMessage() {
        git_repository_message_remove(raw.handler).errorCheck()
    }

    fun cleanUpState() {
        git_repository_state_cleanup(raw.handler).errorCheck()
    }

    val Commit = CommitModule()

    inner class CommitModule {
        fun commit(
            updateRef: String,
            author: Signature,
            committer: Signature,
            message: String,
            tree: Tree,
            parents: List<Commit>,
        ): Oid = Oid {
            git_commit_create(
                this,
                raw.handler,
                updateRef,
                author.raw.handler,
                committer.raw.handler,
                null,
                message,
                tree.raw.handler,
                parents.size.convert(),
                parents.map { it.raw.handler }.toCValues()
            ).errorCheck()
        }

        fun commitCreateBuffer(
            updateRef: String,
            author: Signature,
            committer: Signature,
            message: String,
            tree: Tree,
            parents: List<Commit>,
        ): String? = withGitBuf { buf ->
            git_commit_create_buffer(
                buf,
                raw.handler,
                author.raw.handler,
                committer.raw.handler,
                null,
                message,
                tree.raw.handler,
                parents.size.convert(),
                parents.map { it.raw.handler }.toCValues()
            ).errorCheck()
            buf.toKString()
        }

        fun commitSigned(commitContent: String, signature: String, signatureField: String?): Oid = Oid {
            git_commit_create_with_signature(
                this,
                raw.handler,
                commitContent,
                signature,
                signatureField
            ).errorCheck()
        }

        fun findCommit(oid: Oid): Commit = Commit {
            git_commit_lookup(this.ptr, raw.handler, oid.raw.handler).errorCheck()
        }

        fun findAnnotatedCommit(oid: Oid): AnnotatedCommit = AnnotatedCommit {
            git_annotated_commit_lookup(this.ptr, raw.handler, oid.raw.handler).errorCheck()
        }
    }

    val Checkout = CheckoutModule()

    inner class CheckoutModule {
        fun reset(target: Object, type: ResetType, checkoutOptions: CheckoutOptions) {
            git_reset(raw.handler, target.raw.handler, type.value, checkoutOptions.raw.handler).errorCheck()
        }

        fun resetDefault(target: Object, paths: Collection<String>) {
            withGitStrArray(paths) {
                git_reset_default(raw.handler, target.raw.handler, it).errorCheck()
            }
        }

        fun head(): Reference = Reference {
            git_repository_head(this.ptr, raw.handler).errorCheck()
        }

        fun setHead(refName: String) {
            git_repository_set_head(raw.handler, refName).errorCheck()
        }

        fun detachHead() {
            git_repository_detach_head(raw.handler).errorCheck()
        }

        fun setHeadDetached(commitIsh: Oid) {
            git_repository_set_head_detached(raw.handler, commitIsh.raw.handler).errorCheck()
        }

        fun setHeadDetachedFromAnnotated(annotatedCommit: AnnotatedCommit) {
            git_repository_set_head_detached_from_annotated(raw.handler, annotatedCommit.raw.handler).errorCheck()
        }

        fun checkoutHead(checkoutOptions: CheckoutOptions) {
            git_checkout_head(raw.handler, checkoutOptions.raw.handler).errorCheck()
        }

        fun checkoutIndex(index: Index, checkoutOptions: CheckoutOptions = CheckoutOptions()) {
            git_checkout_index(raw.handler, index.raw.handler, checkoutOptions.raw.handler).errorCheck()
        }

        fun checkoutTree(treeIsh: Object, checkoutOptions: CheckoutOptions = CheckoutOptions()) {
            git_checkout_tree(raw.handler, treeIsh.raw.handler, checkoutOptions.raw.handler).errorCheck()
        }
    }

    val Merge = MergeModule()

    inner class MergeModule {
        /**
         * For compatibility with git, the repository is put into a merging state.
         * Once the commit is done (or if the user wishes to abort),
         * you should clear this state by calling [cleanUpState].
         */
        fun merge(
            annotatedCommits: Collection<AnnotatedCommit>,
            mergeOptions: MergeOptions?,
            checkoutOptions: CheckoutOptions?,
        ) {
            git_merge(
                raw.handler,
                annotatedCommits.map { it.raw.handler }.toCValues(),
                annotatedCommits.size.convert(),
                mergeOptions?.raw?.handler,
                checkoutOptions?.raw?.handler
            ).errorCheck()
        }

        fun mergeCommits(ourCommit: Commit, theirCommit: Commit, mergeOptions: MergeOptions?): Index = Index {
            git_merge_commits(
                this.ptr,
                raw.handler,
                ourCommit.raw.handler,
                theirCommit.raw.handler,
                mergeOptions?.raw?.handler
            ).errorCheck()
        }

        fun mergeTrees(ancestorTree: Tree, ourTree: Tree, theirTree: Tree, mergeOptions: MergeOptions?): Index =
            Index() {
                git_merge_trees(
                    this.ptr,
                    raw.handler,
                    ancestorTree.raw.handler,
                    ourTree.raw.handler,
                    theirTree.raw.handler,
                    mergeOptions?.raw?.handler
                ).errorCheck()
            }

        fun mergeAnalysis(theirHeads: Collection<AnnotatedCommit>): Pair<MergeAnalysisFlag, MergePreferenceFlag> =
            memoryScoped {
                val analysis = alloc<git_merge_analysis_tVar>()
                val preference = alloc<git_merge_preference_tVar>()
                git_merge_analysis(
                    analysis.ptr,
                    preference.ptr,
                    raw.handler,
                    theirHeads.map { it.raw.handler }.toCValues(),
                    theirHeads.size.convert()
                ).errorCheck()
                MergeAnalysisFlag(analysis.value) to MergePreferenceFlag(preference.value)
            }

        fun mergeAnalysisForRef(
            ourRef: Reference,
            theirHeads: Collection<AnnotatedCommit>,
        ): Pair<MergeAnalysisFlag, MergePreferenceFlag> = memoryScoped {
            val analysis = alloc<git_merge_analysis_tVar>()
            val preference = alloc<git_merge_preference_tVar>()
            git_merge_analysis_for_ref(
                analysis.ptr,
                preference.ptr,
                raw.handler,
                ourRef.raw.handler,
                theirHeads.map { it.raw.handler }.toCValues(),
                theirHeads.size.convert()
            ).errorCheck()
            MergeAnalysisFlag(analysis.value) to MergePreferenceFlag(preference.value)
        }

        fun mergeBase(one: Oid, two: Oid): Oid = Oid {
            git_merge_base(this, raw.handler, one.raw.handler, two.raw.handler).errorCheck()
        }

        fun mergeBaseMany(oids: Collection<Oid>): Oid = Oid {
            git_merge_base_many(
                this, raw.handler,
                oids.size.convert(),
                it.allocArrayOf(oids.map { oid -> oid.raw.handler }).pointed.value
            ).errorCheck()
        }

        fun mergeBases(one: Oid, two: Oid): OidArray = OidArray {
            git_merge_bases(
                this,
                raw.handler,
                one.raw.handler,
                two.raw.handler
            ).errorCheck()
        }

        fun mergeBasesMany(oids: Collection<Oid>): OidArray = OidArray {
            git_merge_bases_many(
                this,
                raw.handler,
                oids.size.convert(),
                it.allocArrayOf(oids.map { oid -> oid.raw.handler }).pointed.value
            ).errorCheck()
        }

        fun cherryPick(commit: Commit, options: CherrypickOptions) {
            git_cherrypick(raw.handler, commit.raw.handler, options.raw.handler).errorCheck()
        }

        fun cherryPickCommit(
            cherryPickCommit: Commit,
            ourCommit: Commit,
            mainline: Int,
            options: MergeOptions?,
        ): Index = Index {
            git_cherrypick_commit(
                this.ptr, raw.handler,
                cherryPickCommit.raw.handler,
                ourCommit.raw.handler,
                mainline.convert(),
                options?.raw?.handler
            ).errorCheck()
        }

        fun mergeHeadForeach(callback: (Oid) -> Boolean) {
            val stable = StableRef.create(callback)
            val callback: git_repository_mergehead_foreach_cb = staticCFunction { oidPtr, payload ->
                payload!!.asStableRef<((Oid) -> Boolean)>()
                    .get()
                    .invoke(Oid(Memory(), oidPtr!!))
                    .toInt()
            }
            git_repository_mergehead_foreach(raw.handler, callback, stable.asCPointer()).errorCheck()
            stable.dispose()
        }
    }

    val Rebase = RebaseModule()

    inner class RebaseModule {
        fun rebase(branch: AnnotatedCommit?, upstream: AnnotatedCommit?, onto: AnnotatedCommit?, options: RebaseOptions?): Rebase = Rebase {
            git_rebase_init(this.ptr, raw.handler, branch?.raw?.handler, upstream?.raw?.handler, onto?.raw?.handler, options?.raw?.handler).errorCheck()
        }

        fun openRebase(options: RebaseOptions?): Rebase = Rebase {
            git_rebase_open(this.ptr, raw.handler, options?.raw?.handler).errorCheck()
        }
    }

    val Apply = ApplyModule()

    inner class ApplyModule {
        fun apply(diff: Diff, location: ApplyLocation, options: ApplyOptions?) {
            git_apply(raw.handler, diff.raw.handler, location.value, options?.raw?.handler).errorCheck()
        }

        fun applyToTree(tree: Tree, diff: Diff, options: ApplyOptions?): Index = Index {
            git_apply_to_tree(this.ptr, raw.handler, tree.raw.handler, diff.raw.handler, options?.raw?.handler).errorCheck()
        }
    }

    val Revert = RevertModule()

    inner class RevertModule {
        fun revert(commit: Commit, options: RevertOptions? = null) {
            git_revert(raw.handler, commit.raw.handler, options?.raw?.handler).errorCheck()
        }

        fun revertCommit(revertCommit: Commit, outCommit: Commit, mainline: Int, options: MergeOptions? = null): Index = Index {
            git_revert_commit(this.ptr, raw.handler, revertCommit.raw.handler, outCommit.raw.handler, mainline.convert(), options?.raw?.handler).errorCheck()
        }
    }

    val Branch = BranchModule()

    inner class BranchModule {
        fun createBranch(branchName: String, target: Commit, force: Boolean = false): Branch = Branch {
            git_branch_create(this.ptr, raw.handler, branchName, target.raw.handler, force.toInt()).errorCheck()
        }

        fun createBranchFromAnnotated(
            branchName: String,
            annotatedCommit: AnnotatedCommit,
            force: Boolean = false,
        ): Branch = Branch {
            git_branch_create_from_annotated(
                this.ptr,
                raw.handler,
                branchName,
                annotatedCommit.raw.handler,
                force.toInt()
            ).errorCheck()
        }

        fun branches(listType: BranchType): BranchIterator = BranchIterator {
            git_branch_iterator_new(this.ptr, raw.handler, listType.value).errorCheck()
        }

        fun findBranch(name: String, branchType: BranchType): Branch = Branch {
            git_branch_lookup(this.ptr, raw.handler, name, branchType.value).errorCheck()
        }

        /**
         * @param refName complete name of the remote tracking branch.
         */
        fun branchRemoteName(refName: String): String? = withGitBuf { buf ->
            runCatching {
                git_branch_remote_name(buf, raw.handler, refName).errorCheck()
            }.onFailure {
                if (it is GitError && it.code == GitErrorCode.NotFound) return@withGitBuf null
            }.getOrThrow()
            buf.toKString()
        }

        /**
         * @param refName reference name of the local branch.
         */
        fun branchUpstreamName(refName: String): String? = withGitBuf { buf ->
            runCatching {
                git_branch_upstream_name(buf, raw.handler, refName).errorCheck()
            }.onFailure {
                if (it is GitError && it.code == GitErrorCode.NotFound) return@withGitBuf null
            }.getOrThrow()
            buf.toKString()
        }

        /**
         * @param refName the full name of the branch
         */
        fun branchUpstreamRemote(refName: String): String? = withGitBuf { buf ->
            runCatching {
                git_branch_upstream_remote(buf, raw.handler, refName).errorCheck()
            }.onFailure {
                if (it is GitError && it.code == GitErrorCode.NotFound) return@withGitBuf null
            }.getOrThrow()
            buf.toKString()
        }
    }

    val Tag = TagModule()

    inner class TagModule {
        // fun tag(name: String, target: Object, tagger: Signature, message: String, force: String): Tag/Oid? {
        //     TODO()
        // }

        // fun annotationTag(name: String, target: Object, tagger: Signature, message: String): Tag/Oid? {
        //     TODO()
        // }

        // fun lightweightTag(name: String, target: Object, force: Boolean): Oid = Oid() {
        //     git_tag_create_lightweight(this.ptr, raw.handler, name, target.raw.handler, force.toInt()).errorCheck()
        //     TODO()
        // }

        // fun find(id: Oid): Tag = Tag() {
        //     git_tag_lookup(this.ptr, raw.handler, id.raw.handler).errorCheck()
        //     TODO()
        // }

        fun delete(name: String) {
            git_tag_delete(raw.handler, name).errorCheck()
            TODO()
        }

        // TODO
        // fun tagNames(glob: String): StringIterator = StringIterator() {
        //     git_tag_list_match(this.ptr, glob, raw.handler).errorCheck()
        // }

        // TODO
        // fun forEach(callback: (tag: Tag) -> Unit) {
        //     git_tag_foreach(raw.handler) { tag, _ ->
        //         callback(Tag(tag))
        //         0
        //     }.errorCheck()
        // }
    }

    val Note = NoteModule()

    inner class NoteModule {
        // fun note(author: Signature, committer: Signature, notesRef: String?, oid: Oid, note: String, force: Boolean) {
        //     TODO()
        // }

        // fun notes(notesRef: String?): NoteIterator {
        //     TODO()
        // }

        // fun find(id: Oid, noteRef: String?): Note = Note() {
        //     git_note_read(this.ptr, raw.handler, noteRef, id.raw.handler).errorCheck()
        //     TODO()
        // }

        fun delete(author: Signature, committer: Signature, notesRef: String?, oid: Oid) {
            git_note_remove(raw.handler, notesRef, author.raw.handler, committer.raw.handler, oid.raw.handler)
                .errorCheck()
            TODO()
        }
    }

    val Status = StatusModule()

    inner class StatusModule {
        fun statusList(options: StatusOptions): StatusList = StatusList {
            git_status_list_new(this.ptr, raw.handler, options.raw.handler).errorCheck()
        }

        fun statusShouldIgnore(path: String): Boolean = memoryScoped {
            val ignored = alloc<IntVar>()
            git_status_should_ignore(ignored.ptr, raw.handler, path).toBoolean()
            ignored.value.toBoolean()
        }

        fun statusFile(path: String): StatusFlag = memoryScoped {
            val statusFlags = alloc<git_status_tVar>()
            git_status_file(statusFlags.ptr, raw.handler, path).errorCheck()
            StatusFlag(statusFlags.value)
        }
    }

    val Remote = RemoteModule()

    inner class RemoteModule {
        fun remoteList(): List<String> = withGitStrArray {
            git_remote_list(it, raw.handler).errorCheck()
            it.toList()
        }

        fun findRemote(name: String): Remote = Remote {
            git_remote_lookup(this.ptr, raw.handler, name).errorCheck()
        }

        fun remote(name: String, url: String): Remote = Remote {
            git_remote_create(this.ptr, raw.handler, name, url).errorCheck()
        }

        fun remoteWithFetch(name: String, url: String, fetch: String): Remote = Remote {
            git_remote_create_with_fetchspec(this.ptr, raw.handler, name, url, fetch).errorCheck()
        }

        fun remoteAnonymous(url: String): Remote = Remote {
            git_remote_create_anonymous(this.ptr, raw.handler, url).errorCheck()
        }

        fun remoteDelete(name: String) {
            git_remote_delete(raw.handler, name).errorCheck()
        }

        fun remoteRename(oldName: String, newName: String): List<String> = withGitStrArray {
            git_remote_rename(it, raw.handler, oldName, newName).errorCheck()
            it.toList()
        }

        fun remoteAddFetch(name: String, refspec: String) {
            git_remote_add_fetch(raw.handler, name, refspec).errorCheck()
        }

        fun remoteAddPush(name: String, refspec: String) {
            git_remote_add_push(raw.handler, name, refspec).errorCheck()
        }

        fun remoteSetUrl(name: String, url: String) {
            git_remote_set_url(raw.handler, name, url).errorCheck()
        }

        fun remoteSetPushUrl(name: String, url: String) {
            git_remote_set_pushurl(raw.handler, name, url).errorCheck()
        }

        // fun fetchHeadForeach(callback: (refName: String, remoteUrl: String, remoteTarget: Oid, isMerge: Boolean) -> Boolean) {
        //     git_fetchhead_foreach(raw.handler) { ref, oid, isMerge, _ ->
        //         callback(ref.toKString(), Oid(oid), isMerge.toBoolean())
        //         0
        //     }.errorCheck()
        // }
    }

    val Reference = ReferenceModule()

    inner class ReferenceModule {
        fun reference(name: String, id: Oid, force: Boolean, log_message: String): Reference = Reference {
            git_reference_create(this.ptr, raw.handler, name, id.raw.handler, force.toInt(), log_message).errorCheck()
            TODO()
        }

        fun findReference(name: String): Reference = Reference {
            git_reference_lookup(this.ptr, raw.handler, name).errorCheck()
            TODO()
        }

        fun references(): ReferenceIterator = ReferenceIterator {
            git_reference_iterator_new(this.ptr, raw.handler).errorCheck()
        }

        fun globReferences(glob: String): ReferenceIterator = ReferenceIterator {
            git_reference_iterator_glob_new(this.ptr, raw.handler, glob).errorCheck()
        }

        fun referenceMatching(name: String, id: Oid, force: Boolean, current_id: Oid, log_message: String): Reference =
            Reference {
                git_reference_create_matching(
                    this.ptr,
                    raw.handler,
                    name,
                    id.raw.handler,
                    force.toInt(),
                    current_id.raw.handler,
                    log_message
                ).errorCheck()
                TODO()
            }

        fun referenceSymbolic(name: String, target: String, force: Boolean, log_message: String): Reference =
            Reference {
                git_reference_symbolic_create(
                    this.ptr,
                    raw.handler,
                    name,
                    target,
                    force.toInt(),
                    log_message
                ).errorCheck()
                TODO()
            }

        fun referenceSymbolicMatching(
            name: String,
            target: String,
            force: Boolean,
            currentValue: String,
            log_message: String,
        ): Reference = Reference {
            TODO()
        }

        fun resolveReferenceFromShortName(shortName: String): Reference = Reference {
            TODO()
        }

        fun referenceToAnnotatedCommit(reference: Reference): AnnotatedCommit {
            TODO()
        }
    }

    val Oid = OidModule()

    inner class OidModule {
        fun refNameToOid(refName: String): Oid {
            TODO()
        }
    }

    val Object = ObjectModule()

    inner class ObjectModule {
        fun findObject(oid: Oid, type: ObjectType? = null): Object = Object {
            git_object_lookup(this.ptr, raw.handler, oid.raw.handler, type?.value ?: ObjectType.Any.value).errorCheck()
            TODO()
        }
    }

    val Stash = StashModule()

    inner class StashModule {
        // fun stash(stasher: Signature, message: String, flags: StashFlags): Oid {
        //     TODO()
        // }

        // fun apply(index: Int, options: StashApplyOptions): Oid {
        //     TODO()
        // }

        // fun forEach(callback: (index: Int, message: String, stashId: Oid) -> Unit) {
        //     TODO()
        // }

        // fun drop(index: Int) {
        //     git_stash_drop(raw.handler, index).errorCheck()
        //     TODO()
        // }

        // fun pop(index: Int, options: StashApplyOptions) {
        //     TODO()
        // }
    }

    val Config = ConfigModule()

    inner class ConfigModule {
        fun config(): Config = Config {
            git_repository_config(this.ptr, raw.handler).errorCheck()
        }
    }

    val Signature = SignatureModule()

    inner class SignatureModule {
        fun signatureNow(name: String, email: String): Signature = Signature {
            git_signature_now(this.ptr, name, email).errorCheck()
            TODO()
        }

        fun signatureDefault(): Signature = Signature {
            git_signature_default(this.ptr, raw.handler).errorCheck()
            TODO()
        }

        fun extractSignature(commitId: Oid, signatureField: String?) {
            TODO()
        }
    }

    val AnnotatedCommit = AnnotatedCommitModule()

    inner class AnnotatedCommitModule {
        // fun annotatedCommitFromFetchHead(branchName: String, remoteUrl: String, id: Oid): AnnotatedCommit =
        //     AnnotatedCommit() {
        //         git_annotated_commit_from_fetchhead(this.ptr, raw.handler, branchName, refName, remoteUrl).errorCheck()
        //         TODO()
        //     }
    }

    val Ignore = IgnoreModule()

    inner class IgnoreModule {
        fun addRule(rules: String) {
            git_ignore_add_rule(raw.handler, rules).errorCheck()
            TODO()
        }

        fun clearRules() {
            git_ignore_clear_internal_rules(raw.handler).errorCheck()
            TODO()
        }

        fun isIgnored(path: String): Boolean {
            TODO()
        }
    }

    val Submodule = SubmoduleModule()

    inner class SubmoduleModule {
        fun submodules(): List<Submodule> {
            val gitCallback: git_submodule_cb = staticCFunction { _, name, payload ->
                val callbackPayload = payload!!.asStableRef<Pair<RepositoryPointer, MutableList<Submodule>>>()
                val (repository, submodules) = callbackPayload.get()
                submodules.add(Submodule {
                    git_submodule_lookup(this.ptr, repository, name?.toKString()).errorCheck()
                })
                callbackPayload.dispose()
                0
            }
            val submodules = mutableListOf<Submodule>()
            git_submodule_foreach(
                raw.handler,
                gitCallback,
                StableRef.create(raw.handler to submodules).asCPointer()
            ).errorCheck()
            return submodules
        }

        fun updateSubmodules() {
            fun addSubRepos(repo: Repository, list: MutableList<Repository>) {
                for (subm in repo.Submodule.submodules()) {
                    subm.update(true)
                    list.add(subm.open())
                }
            }

            val list = mutableListOf<Repository>()
            addSubRepos(this@Repository, list)
            while (list.isNotEmpty()) {
                val repo = list.removeAt(0)
                addSubRepos(repo, list)
            }
        }

        fun submoduleSetBranch(name: String, branch: String) {
            git_submodule_set_branch(raw.handler, name, branch).errorCheck()
        }

        fun findSubmodule(name: String): Submodule = Submodule {
            git_submodule_lookup(this.ptr, raw.handler, name).errorCheck()
            TODO()
        }

        // TODO()
        // fun submoduleStatus(name: String, ignore: SubmoduleIgnore): SubmoduleStatus {
        //     val status = alloc<git_submodule_status_tVar>()
        //     git_submodule_status(status.ptr, raw.handler, name, ignore.value).errorCheck()
        //     return SubmoduleStatus(status.value)
        // }

        // TODO()
        // fun submoduleSetIgnore(name: String, ignore: SubmoduleIgnore) {
        //     git_submodule_set_ignore(raw.handler, name, ignore.value).errorCheck()
        // }

        //TODO()
        // fun submoduleSetUpdate(name: String, update: SubmoduleUpdate) {
        //     git_submodule_set_update(raw.handler, name, update.value).errorCheck()
        // }

        fun submoduleSetUrl(name: String, url: String) {
            git_submodule_set_url(raw.handler, name, url).errorCheck()
            TODO()
        }
    }

    val Diff = DiffModule()

    inner class DiffModule {
        // fun diffTreeToTree(oldTree: Tree, newTree: Tree, options: DiffOptions? = null): Diff = Diff() {
        //     TODO()
        //     git_diff_tree_to_tree(
        //         this.ptr,
        //         raw.handler,
        //         oldTree.raw.handler,
        //         newTree.raw.handler,
        //         options?.raw
        //     ).errorCheck()
        // }

        // fun diffTreeToIndex(oldTree: Tree, index: Index, options: DiffOptions? = null): Diff = Diff() {
        //     TODO()
        //     git_diff_tree_to_index(
        //         this.ptr,
        //         raw.handler,
        //         oldTree.raw.handler,
        //         index.raw.handler,
        //         options?.raw
        //     ).errorCheck()
        // }

        // fun diffIndexToIndex(oldIndex: Index, newIndex: Index, options: DiffOptions? = null): Diff = Diff() {
        //     TODO()
        //     git_diff_index_to_index(this.ptr, oldIndex.raw.handler, newIndex.raw.handler, options?.raw).errorCheck()
        // }

        // fun diffIndexToWorkdir(index: Index, options: DiffOptions? = null): Diff = Diff() {
        //     TODO()
        //     git_diff_index_to_workdir(this.ptr, raw.handler, index.raw.handler, options?.raw).errorCheck()
        // }

        // fun diffTreeToWorkdir(oldTree: Tree, options: DiffOptions? = null): Diff = Diff() {
        //     TODO()
        //     git_diff_tree_to_workdir(this.ptr, raw.handler, oldTree.raw.handler, options?.raw).errorCheck()
        // }

        // fun diffTreeToWorkdirWithIndex(oldTree: Tree, options: DiffOptions? = null): Diff = Diff() {
        //     TODO()
        //     git_diff_tree_to_workdir_with_index(this.ptr, raw.handler, oldTree.raw.handler, options?.raw).errorCheck()
        // }

        // fun diffBlobs(oldBlob: Blob, oldAsPath: String, newBlob: Blob, newAsPath: String, options: DiffOptions? = null, file_cb: Option<&mut FileCb<'_>>, binaryCallback: BinaryCallback?, hunkCallback: HunkCallback?, lineCallback: LineCallback?): Diff = Diff() {
        //     TODO()
        //     git_diff_blobs(
        //         this.ptr,
        //         oldBlob.raw.handler,
        //         oldAsPath,
        //         newBlob.raw.handler,
        //         newAsPath,
        //         options?.raw
        //     ).errorCheck()
        // }
    }

    val Index = IndexModule()

    inner class IndexModule {
        fun index(): Index = Index() {
            git_repository_index(this.ptr, raw.handler).errorCheck()
        }

        fun setIndex(index: Index) {
            git_repository_set_index(raw.handler, index.raw.handler).errorCheck()
        }
    }

    val Blob = BlobModule()

    inner class BlobModule {
        fun blob(data: ByteArray): Oid {
            TODO()
        }

        fun findBlob(oid: Oid): Blob {
            TODO()
        }

        fun blobPath(path: String): Oid {
            TODO()
        }

        // fun blobWriter(hintPath: String? = null): BlobWriter {
        //     TODO()
        // }
    }

    val Tree = TreeModule()

    inner class TreeModule {
        fun findTree(oid: Oid): Tree {
            TODO()
        }

        // TODO
        // fun treeBuilder(tree: Tree?): TreeBuilder
    }

    val Worktree = WorktreeModule()

    inner class WorktreeModule {
        // fun worktree(name: String, path: String, options: WorktreeAddOptions): Worktree {
        //     TODO()
        // }

        // TODO()
        // fun worktrees(): List<String> {
        // }

        // TODO()
        // fun find(name: String) {
        // }
    }

    val Transaction = TransactionModule()

    inner class TransactionModule {
        // fun transaction(): Transaction {
        //     TODO()
        // }
    }

    val Odb = OdbModule()

    inner class OdbModule {
        fun odb(): Odb {
            TODO()
        }

        fun setOdb(oid: Oid, odb: Odb) {
            TODO()
        }
    }

    val Rev = RevModule()

    inner class RevModule {
        // fun revWalk(): RevWalk {
        //     TODO()
        // }

        fun revParse(spec: String): RevSpec = RevSpec {
            git_revparse(this, raw.handler, spec).errorCheck()
        }

        fun revParseSingle(spec: String) = Object {
            git_revparse_single(this.ptr, raw.handler, spec).errorCheck()
        }

        fun revParseExt(spec: String): Pair<Object, Reference> {
            lateinit var reference: Reference
            val `object` = Object {
                reference = Reference {
                    git_revparse_ext(this@Object.ptr, this@Reference.ptr, raw.handler, spec).errorCheck()
                }
            }
            return `object` to reference
        }
    }

    val Graph = GraphModule()

    inner class GraphModule {
        fun graphAheadBehind(local: Oid, upstream: Oid): Pair<Int, Int> {
            TODO()
        }

        fun graphDescendantOf(commit: Oid, ancestor: Oid): Boolean {
            TODO()
        }
    }

    val Reflog = ReflogModule()

    inner class ReflogModule {
        // fun read(name: String): Reflog {
        //     TODO()
        // }

        fun rename(oldName: String, newName: String) {
            TODO()
        }

        fun delete(name: String) {
            TODO()
        }

        fun referenceHasLog(name: String): Boolean {
            TODO()
        }

        fun referenceEnsureLog(name: String) {
            TODO()
        }
    }

    val Blame = BlameModule()

    inner class BlameModule {
        // fun blame(path: String, options: BlamOptions): Blame {
        //     TODO()
        // }
    }

    val Describe = DescribeModule()

    inner class DescribeModule {
        // fun describe(options: DescribeOptions): Describe {
        //     TODO()
        // }
    }

    val Attr = AttrModule()

    inner class AttrModule {
        // TODO()
        // fun getAttr(path: String, name: String, flags: AttrCheckFlags): String? = withGitBuf { buf ->
        //     runCatching {
        //         git_attr_get(buf, raw.handler, 0, path, name).errorCheck()
        //     }.onFailure {
        //         if (it is GitError && it.code == GitErrorCode.NotFound) return@withGitBuf null
        //     }.getOrThrow()
        //     buf.toKString()
        // }
    }

    val PackBuilder = PackBuilderModule()

    inner class PackBuilderModule {
        // fun packBuilder(): PackBuilder {
        //     TODO()
        // }
    }

    val MailMap = MailMapModule()

    inner class MailMapModule {
        // fun mailMap(): MailMap {
        //     TODO()
        // }
    }
}
