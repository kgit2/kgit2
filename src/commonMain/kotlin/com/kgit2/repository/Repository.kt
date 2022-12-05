@file:Suppress("PropertyName", "MemberVisibilityCanBePrivate")

package com.kgit2.repository

import cnames.structs.git_repository
import com.kgit2.annotations.Raw
import com.kgit2.apply.ApplyLocation
import com.kgit2.apply.ApplyOptions
import com.kgit2.attr.*
import com.kgit2.blame.Blame
import com.kgit2.blame.BlameOptions
import com.kgit2.blob.Blob
import com.kgit2.blob.BlobWriter
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
import com.kgit2.common.extend.*
import com.kgit2.common.memory.Memory
import com.kgit2.common.memory.memoryScoped
import com.kgit2.config.Config
import com.kgit2.describe.Describe
import com.kgit2.describe.DescribeOptions
import com.kgit2.diff.*
import com.kgit2.index.Index
import com.kgit2.mailmap.Mailmap
import com.kgit2.memory.RawWrapper
import com.kgit2.merge.MergeAnalysisFlag
import com.kgit2.merge.MergeOptions
import com.kgit2.merge.MergePreferenceFlag
import com.kgit2.model.Buf
import com.kgit2.model.StrArray
import com.kgit2.model.StrarrayRaw
import com.kgit2.model.toStrArray
import com.kgit2.note.Note
import com.kgit2.note.NoteIterator
import com.kgit2.`object`.Object
import com.kgit2.`object`.ObjectType
import com.kgit2.odb.Odb
import com.kgit2.oid.Oid
import com.kgit2.oid.OidArray
import com.kgit2.packbuilder.Packbuilder
import com.kgit2.rebase.Rebase
import com.kgit2.rebase.RebaseOptions
import com.kgit2.reference.Reference
import com.kgit2.reference.ReferenceIterator
import com.kgit2.reflog.Reflog
import com.kgit2.remote.Remote
import com.kgit2.rev.RevSpec
import com.kgit2.rev.Revwalk
import com.kgit2.revert.RevertOptions
import com.kgit2.signature.Signature
import com.kgit2.stash.*
import com.kgit2.status.StatusFlag
import com.kgit2.status.StatusList
import com.kgit2.status.StatusOptions
import com.kgit2.submodule.Submodule
import com.kgit2.submodule.SubmoduleIgnore
import com.kgit2.submodule.SubmoduleStatus
import com.kgit2.submodule.SubmoduleUpdate
import com.kgit2.tag.Tag
import com.kgit2.tag.TagForeachCallback
import com.kgit2.tag.TagForeachCallbackPayload
import com.kgit2.tag.staticTagForeachCallback
import com.kgit2.transaction.Transaction
import com.kgit2.tree.Tree
import com.kgit2.tree.TreeBuilder
import com.kgit2.worktree.Worktree
import com.kgit2.worktree.WorktreeAddOptions
import kotlinx.cinterop.*
import libgit2.*

@Raw(
    base = git_repository::class,
    free = "git_repository_free",
)
class Repository(raw: RepositoryRaw) : RawWrapper<git_repository, RepositoryRaw>(raw) {
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

        fun open(path: String, bare: Boolean = false): Repository = Repository {
            when (bare) {
                true -> git_repository_open_bare(this.ptr, path)
                false -> git_repository_open(this.ptr, path)
            }.errorCheck()
        }

        fun initialExt(path: String, options: RepositoryInitOptions): Repository = Repository {
            git_repository_init_ext(this.ptr, path, options.raw.handler).errorCheck()
        }

        fun openExt(
            path: String,
            flags: RepositoryOpenFlags,
            ceilingDirs: String? = null,
        ): Repository = Repository {
            git_repository_open_ext(this.ptr, path, flags.flags, ceilingDirs).errorCheck()
        }

        fun openFromWorktree(worktree: Worktree): Repository = Repository {
            git_repository_open_from_worktree(this.ptr, worktree.raw.handler).errorCheck()
        }

        fun openFromEnv(): Repository = Repository {
            git_repository_open_ext(this.ptr, null, RepositoryOpenFlags().flags, null).errorCheck()
        }

        fun discover(path: String): Repository {
            val discoverPath = Buf {
                git_repository_discover(this, path, 1, null).errorCheck()
            }
            return open(discoverPath.buffer!!.toKString())
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

    val path: String
        get() = git_repository_path(raw.handler)!!.toKString()

    val isBare: Boolean
        get() = git_repository_is_bare(raw.handler).toBoolean()

    val isShallow: Boolean
        get() = git_repository_is_shallow(raw.handler).toBoolean()

    val isWorkTree: Boolean
        get() = git_repository_is_worktree(raw.handler).toBoolean()

    val isEmpty: Boolean
        get() = git_repository_is_empty(raw.handler).toBoolean()

    val noteDefaultRef: String = with(Buf {
        git_note_default_ref(this, raw.handler).errorCheck()
    }) {
        this.buffer!!.toKString()
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

    val message: String? = with(runCatching {
        Buf {
            git_repository_message(this, raw.handler).errorCheck()
        }
    }.onFailure {
        if (it !is GitError || it.code != GitErrorCode.NotFound) throw it
    }.getOrNull()) {
        this?.buffer?.toKString()
    }

    fun removeMessage() {
        git_repository_message_remove(raw.handler).errorCheck()
    }

    fun cleanUpState() {
        git_repository_state_cleanup(raw.handler).errorCheck()
    }

    fun describe(options: DescribeOptions): Describe = Describe(this, options)

    fun blame(path: String, options: BlameOptions): Blame = Blame(this, path, options)

    fun transaction(): Transaction = Transaction(this)

    fun packBuilder(): Packbuilder = Packbuilder(this)

    fun mailMap(): Mailmap = Mailmap(this)

    val Commit = CommitModule()

    inner class CommitModule {
        fun commit(
            updateRef: String,
            author: Signature,
            committer: Signature,
            message: String,
            tree: Tree,
            parents: List<Commit> = emptyList(),
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
        ): Buf? = runCatching {
            Buf {
                git_commit_create_buffer(
                    this,
                    raw.handler,
                    author.raw.handler,
                    committer.raw.handler,
                    null,
                    message,
                    tree.raw.handler,
                    parents.size.convert(),
                    parents.map { it.raw.handler }.toCValues()
                ).errorCheck()
            }
        }.getOrNull()

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

        fun resetDefault(target: Object, paths: Collection<String>) =
            git_reset_default(raw.handler, target.raw.handler, paths.toStrArray().raw.handler).errorCheck()

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
        fun rebase(
            branch: AnnotatedCommit?,
            upstream: AnnotatedCommit?,
            onto: AnnotatedCommit?,
            options: RebaseOptions?,
        ): Rebase = Rebase {
            git_rebase_init(
                this.ptr,
                raw.handler,
                branch?.raw?.handler,
                upstream?.raw?.handler,
                onto?.raw?.handler,
                options?.raw?.handler
            ).errorCheck()
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
            git_apply_to_tree(
                this.ptr,
                raw.handler,
                tree.raw.handler,
                diff.raw.handler,
                options?.raw?.handler
            ).errorCheck()
        }
    }

    val Revert = RevertModule()

    inner class RevertModule {
        fun revert(commit: Commit, options: RevertOptions? = null) {
            git_revert(raw.handler, commit.raw.handler, options?.raw?.handler).errorCheck()
        }

        fun revertCommit(revertCommit: Commit, outCommit: Commit, mainline: Int, options: MergeOptions? = null): Index =
            Index {
                git_revert_commit(
                    this.ptr,
                    raw.handler,
                    revertCommit.raw.handler,
                    outCommit.raw.handler,
                    mainline.convert(),
                    options?.raw?.handler
                ).errorCheck()
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
        fun branchRemoteName(refName: String): String? = with(
            runCatching {
                Buf { git_branch_remote_name(this, raw.handler, refName).errorCheck() }
            }.onFailure {
                if (it !is GitError || it.code != GitErrorCode.NotFound) throw it
            }.getOrNull()
        ) {
            this?.buffer?.toKString()
        }

        /**
         * @param refName reference name of the local branch.
         */
        fun branchUpstreamName(refName: String): String? = with(
            runCatching {
                Buf { git_branch_upstream_name(this, raw.handler, refName).errorCheck() }
            }.onFailure {
                if (it !is GitError || it.code != GitErrorCode.NotFound) throw it
            }.getOrNull()
        ) {
            this?.buffer?.toKString()
        }

        /**
         * @param refName the full name of the branch
         */
        fun branchUpstreamRemote(refName: String): String? = with(
            runCatching {
                Buf { git_branch_upstream_remote(this, raw.handler, refName).errorCheck() }
            }.onFailure {
                if (it !is GitError || it.code != GitErrorCode.NotFound) throw it
            }.getOrNull()
        ) {
            this?.buffer?.toKString()
        }
    }

    val Tag = TagModule()

    inner class TagModule {
        fun createTag(name: String, target: Object, tagger: Signature, message: String, force: String): Oid = Oid {
            git_tag_create(
                this,
                raw.handler,
                name,
                target.raw.handler,
                tagger.raw.handler,
                message,
                force.toInt()
            ).errorCheck()
        }

        fun annotationTag(name: String, target: Object, tagger: Signature, message: String): Oid = Oid {
            git_tag_annotation_create(
                this,
                raw.handler,
                name,
                target.raw.handler,
                tagger.raw.handler,
                message
            ).errorCheck()
        }

        fun lightweightTag(name: String, target: Object, force: Boolean): Oid = Oid {
            git_tag_create_lightweight(this, raw.handler, name, target.raw.handler, force.toInt()).errorCheck()
        }

        fun find(id: Oid): Tag = Tag {
            git_tag_lookup(this.ptr, raw.handler, id.raw.handler).errorCheck()
        }

        fun delete(name: String) {
            git_tag_delete(raw.handler, name).errorCheck()
        }

        fun tagNames(glob: String): StrArray = StrArray(StrarrayRaw(initial = {
            git_tag_list_match(this, glob, raw.handler).errorCheck()
        }))

        fun forEach(callback: TagForeachCallback) {
            val callbackPayload = object : TagForeachCallbackPayload {
                override var tagForeachCallback: TagForeachCallback? = callback
            }.asStableRef()
            git_tag_foreach(
                raw.handler,
                staticTagForeachCallback,
                callbackPayload.asCPointer()
            ).errorCheck()
            callbackPayload.dispose()
        }
    }

    val Note = NoteModule()

    inner class NoteModule {
        fun note(
            author: Signature,
            committer: Signature,
            notesRef: String?,
            oid: Oid,
            note: String,
            force: Boolean,
        ): Oid = Oid {
            git_note_create(
                this,
                raw.handler,
                notesRef,
                author.raw.handler,
                committer.raw.handler,
                oid.raw.handler,
                note,
                force.toInt()
            ).errorCheck()
        }

        fun notes(notesRef: String?): NoteIterator = NoteIterator {
            git_note_iterator_new(this.ptr, raw.handler, notesRef).errorCheck()
        }

        fun find(id: Oid, notesRef: String?): Note = Note() {
            git_note_read(this.ptr, raw.handler, notesRef, id.raw.handler).errorCheck()
        }

        fun delete(author: Signature, committer: Signature, notesRef: String?, oid: Oid) {
            git_note_remove(raw.handler, notesRef, author.raw.handler, committer.raw.handler, oid.raw.handler)
                .errorCheck()
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
        fun remoteList(): StrArray = StrArray(StrarrayRaw(initial = {
            git_remote_list(this, raw.handler).errorCheck()
        }))

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

        fun remoteRename(oldName: String, newName: String): StrArray = StrArray(StrarrayRaw(initial = {
            git_remote_rename(this, raw.handler, oldName, newName).errorCheck()
        }))

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

        fun fetchHeadForeach(callback: RepositoryFetchHeadForeachCallback) {
            val callbackPayload = object : RepositoryFetchHeadForeachCallbackPayload {
                override var repositoryFetchHeadForeachCallback: RepositoryFetchHeadForeachCallback? = callback
            }.asStableRef()
            git_repository_fetchhead_foreach(
                raw.handler,
                staticRepositoryFetchHeadForeachCallback,
                callbackPayload.asCPointer()
            )
                .errorCheck()
            callbackPayload.dispose()
        }
    }

    val Reference = ReferenceModule()

    inner class ReferenceModule {
        fun reference(name: String, id: Oid, force: Boolean, log_message: String): Reference = Reference {
            git_reference_create(this.ptr, raw.handler, name, id.raw.handler, force.toInt(), log_message).errorCheck()
        }

        fun findReference(name: String): Reference = Reference {
            git_reference_lookup(this.ptr, raw.handler, name).errorCheck()
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
            }

        fun referenceSymbolicMatching(
            name: String,
            target: String,
            force: Boolean,
            currentValue: String,
            log_message: String,
        ): Reference = Reference {
            git_reference_symbolic_create_matching(
                this.ptr,
                raw.handler,
                name,
                target,
                force.toInt(),
                currentValue,
                log_message
            ).errorCheck()
        }

        fun resolveReferenceFromShortName(shortName: String): Reference = Reference {
            git_reference_dwim(this.ptr, raw.handler, shortName).errorCheck()
        }

        fun referenceToAnnotatedCommit(reference: Reference): AnnotatedCommit = AnnotatedCommit {
            git_annotated_commit_from_ref(this.ptr, raw.handler, reference.raw.handler).errorCheck()
        }

        fun referenceHasLog(name: String): Boolean {
            return when (val result = git_reference_has_log(raw.handler, name)) {
                1 -> true
                0 -> false
                else -> throw GitError(GitErrorCode.fromRaw(result))
            }
        }

        fun referenceEnsureLog(name: String) {
            return git_reference_ensure_log(raw.handler, name).errorCheck()
        }
    }

    val Oid = OidModule()

    inner class OidModule {
        fun refNameToOid(refName: String): Oid = Oid {
            git_reference_name_to_id(this, raw.handler, refName).errorCheck()
        }
    }

    val Object = ObjectModule()

    inner class ObjectModule {
        fun findObject(oid: Oid, type: ObjectType = ObjectType.Any): Object = Object {
            git_object_lookup(this.ptr, raw.handler, oid.raw.handler, type.value).errorCheck()
        }
    }

    val Stash = StashModule()

    inner class StashModule {
        fun stash(stasher: Signature, message: String, flags: StashFlags): Oid = Oid {
            git_stash_save(this, raw.handler, stasher.raw.handler, message, flags.value).errorCheck()
        }

        fun apply(index: ULong, options: StashApplyOptions) {
            git_stash_apply(raw.handler, index, options.raw.handler).errorCheck()
        }

        fun pop(index: ULong, options: StashApplyOptions) {
            git_stash_pop(raw.handler, index, options.raw.handler).errorCheck()
        }

        fun forEach(callback: StashCallback) {
            val callbackPayload = object : StashCallbackPayload {
                override var stashCallback: StashCallback? = callback
            }.asStableRef()
            git_stash_foreach(raw.handler, staticStashCallback, callbackPayload.asCPointer()).errorCheck()
            callbackPayload.dispose()
        }

        fun drop(index: ULong) {
            git_stash_drop(raw.handler, index).errorCheck()
        }
    }

    val Config = ConfigModule()

    inner class ConfigModule {
        fun config(): Config = Config {
            git_repository_config(this.ptr, raw.handler).errorCheck()
        }
    }

    val Signature = SignatureModule()

    inner class SignatureModule {
        fun signature(): Signature = Signature {
            git_signature_default(this.ptr, raw.handler).errorCheck()
        }
        fun signatureNow(name: String, email: String): Signature = Signature {
            git_signature_now(this.ptr, name, email).errorCheck()
        }


        fun extractSignature(commitId: Oid, signatureField: String?): Pair<Buf, Buf> {
            lateinit var signature: Buf
            lateinit var signData: Buf
            signature = Buf sig@{
                signData = Buf data@{
                    git_commit_extract_signature(
                        this@sig,
                        this@data,
                        raw.handler,
                        commitId.raw.handler,
                        signatureField
                    ).errorCheck()
                }
            }
            return signature to signData
        }
    }

    val AnnotatedCommit = AnnotatedCommitModule()

    inner class AnnotatedCommitModule {
        fun annotatedCommitFromFetchHead(branchName: String, remoteUrl: String, id: Oid): AnnotatedCommit =
            AnnotatedCommit() {
                git_annotated_commit_from_fetchhead(
                    this.ptr,
                    raw.handler,
                    branchName,
                    remoteUrl,
                    id.raw.handler
                ).errorCheck()
            }
    }

    val Ignore = IgnoreModule()

    inner class IgnoreModule {
        fun addRule(rules: String) {
            git_ignore_add_rule(raw.handler, rules).errorCheck()
        }

        fun clearRules() {
            git_ignore_clear_internal_rules(raw.handler).errorCheck()
        }

        fun isIgnored(path: String): Boolean = memoryScoped {
            val result = alloc<IntVar>()
            git_ignore_path_is_ignored(result.ptr, raw.handler, path).errorCheck()
            result.value.toBoolean()
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
        }

        fun submoduleStatus(name: String, ignore: SubmoduleIgnore): SubmoduleStatus = memoryScoped {
            val status = alloc<git_submodule_status_tVar>()
            git_submodule_status(status.ptr, raw.handler, name, ignore.value).errorCheck()
            return SubmoduleStatus(status.value)
        }

        fun submoduleSetIgnore(name: String, ignore: SubmoduleIgnore) {
            git_submodule_set_ignore(raw.handler, name, ignore.value).errorCheck()
        }

        fun submoduleSetUpdate(name: String, update: SubmoduleUpdate) {
            git_submodule_set_update(raw.handler, name, update.value).errorCheck()
        }

        fun submoduleSetUrl(name: String, url: String) {
            git_submodule_set_url(raw.handler, name, url).errorCheck()
        }
    }

    val Diff = DiffModule()

    inner class DiffModule {
        fun diffTreeToTree(oldTree: Tree, newTree: Tree, options: DiffOptions? = null): Diff = Diff() {
            git_diff_tree_to_tree(
                this.ptr,
                raw.handler,
                oldTree.raw.handler,
                newTree.raw.handler,
                options?.raw?.handler
            ).errorCheck()
        }

        fun diffTreeToIndex(oldTree: Tree, index: Index, options: DiffOptions? = null): Diff = Diff() {
            git_diff_tree_to_index(
                this.ptr,
                raw.handler,
                oldTree.raw.handler,
                index.raw.handler,
                options?.raw?.handler
            ).errorCheck()
        }

        fun diffIndexToIndex(oldIndex: Index, newIndex: Index, options: DiffOptions? = null): Diff = Diff() {
            git_diff_index_to_index(
                this.ptr,
                raw.handler,
                oldIndex.raw.handler,
                newIndex.raw.handler,
                options?.raw?.handler
            ).errorCheck()
        }

        fun diffIndexToWorkdir(index: Index? = null, options: DiffOptions? = null): Diff = Diff() {
            git_diff_index_to_workdir(this.ptr, raw.handler, index?.raw?.handler, options?.raw?.handler).errorCheck()
        }

        fun diffTreeToWorkdir(oldTree: Tree? = null, options: DiffOptions? = null): Diff = Diff() {
            git_diff_tree_to_workdir(this.ptr, raw.handler, oldTree?.raw?.handler, options?.raw?.handler).errorCheck()
        }

        fun diffTreeToWorkdirWithIndex(oldTree: Tree? = null, options: DiffOptions? = null): Diff = Diff() {
            git_diff_tree_to_workdir_with_index(
                this.ptr,
                raw.handler,
                oldTree?.raw?.handler,
                options?.raw?.handler
            ).errorCheck()
        }

        fun diffBlobs(
            oldBlob: Blob,
            oldAsPath: String,
            newBlob: Blob,
            newAsPath: String,
            options: DiffOptions? = null,
            fileCallback: DiffFileCallback? = null,
            binaryCallback: DiffBinaryCallback? = null,
            hunkCallback: DiffHunkCallback? = null,
            lineCallback: DiffLineCallback? = null,
        ) {
            val callbackPayload = object : DiffFileCallbackPayload, DiffBinaryCallbackPayload, DiffHunkCallbackPayload,
                DiffLineCallbackPayload {
                override var diffFileCallback: DiffFileCallback? = fileCallback
                override var diffBinaryCallback: DiffBinaryCallback? = binaryCallback
                override var diffHunkCallback: DiffHunkCallback? = hunkCallback
                override var diffLineCallback: DiffLineCallback? = lineCallback
            }.asStableRef()
            git_diff_blobs(
                oldBlob.raw.handler,
                oldAsPath,
                newBlob.raw.handler,
                newAsPath,
                options?.raw?.handler,
                staticDiffFileCallback,
                staticDiffBinaryCallback,
                staticDiffHunkCallback,
                staticDiffLineCallback,
                callbackPayload.asCPointer()
            ).errorCheck()
            callbackPayload.dispose()
        }
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
        fun blob(data: ByteArray): Oid = Oid {
            git_blob_create_from_buffer(this, raw.handler, data.refTo(0), data.size.toULong()).errorCheck()
        }

        fun find(oid: Oid): Blob = Blob {
            git_blob_lookup(this.ptr, raw.handler, oid.raw.handler).errorCheck()
        }

        fun createByPath(path: String): Oid = Oid {
            git_blob_create_fromdisk(this, raw.handler, path).errorCheck()
        }

        fun writer(hintPath: String? = null): BlobWriter = BlobWriter {
            git_blob_create_from_stream(this.ptr, raw.handler, hintPath).errorCheck()
        }
    }

    val Tree = TreeModule()

    inner class TreeModule {
        fun findTree(oid: Oid): Tree = Tree {
            git_tree_lookup(this.ptr, raw.handler, oid.raw.handler).errorCheck()
        }

        fun treeBuilder(tree: Tree?): TreeBuilder = TreeBuilder {
            git_treebuilder_new(this.ptr, raw.handler, tree?.raw?.handler).errorCheck()
        }
    }

    val Worktree = WorktreeModule()

    inner class WorktreeModule {
        fun worktree(name: String, path: String, options: WorktreeAddOptions): Worktree = Worktree {
            git_worktree_add(this.ptr, raw.handler, name, path, options.raw.handler).errorCheck()
        }

        fun worktrees(): StrArray = StrArray {
            git_worktree_list(this, raw.handler).errorCheck()
        }

        fun find(name: String): Worktree = Worktree {
            git_worktree_lookup(this.ptr, raw.handler, name).errorCheck()
        }
    }

    val Odb = OdbModule()

    inner class OdbModule {
        fun odb(): Odb = Odb {
            git_repository_odb(this.ptr, raw.handler).errorCheck()
        }

        fun setOdb(oid: Oid, odb: Odb) {
            git_repository_set_odb(raw.handler, odb.raw.handler).errorCheck()
        }
    }

    val Rev = RevModule()

    inner class RevModule {
        fun revWalk(): Revwalk = Revwalk {
            git_revwalk_new(this.ptr, raw.handler).errorCheck()
        }

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
        fun graphAheadBehind(local: Oid, upstream: Oid): Pair<ULong, ULong> = memoryScoped {
            val ahead = alloc<ULongVar>()
            val behind = alloc<ULongVar>()
            git_graph_ahead_behind(
                ahead.ptr,
                behind.ptr,
                raw.handler,
                local.raw.handler,
                upstream.raw.handler
            ).errorCheck()
            ahead.value to behind.value
        }

        fun graphDescendantOf(commit: Oid, ancestor: Oid): Boolean {
            return when (val result = git_graph_descendant_of(raw.handler, commit.raw.handler, ancestor.raw.handler)) {
                1 -> true
                0 -> false
                else -> throw GitError(GitErrorCode.fromRaw(result))
            }
        }
    }

    val Reflog = ReflogModule()

    inner class ReflogModule {
        fun read(name: String): Reflog = Reflog(this@Repository, name)

        fun rename(oldName: String, newName: String) {
            git_reflog_rename(raw.handler, oldName, newName).errorCheck()
        }

        fun delete(name: String) {
            git_reflog_delete(raw.handler, name).errorCheck()
        }
    }

    val Attribute = AttributeModule()

    inner class AttributeModule {
        fun getAttr(path: String, name: String, flags: AttrCheckFlags): String? = memoryScoped {
            val out = allocPointerTo<ByteVar>()
            git_attr_get(out.ptr, raw.handler, flags.value, path, name).errorCheck()
            out.value?.toKString()
        }

        fun getExt(path: String, name: String, options: AttrOptions): String? = memoryScoped {
            val out = allocPointerTo<ByteVar>()
            git_attr_get_ext(out.ptr, raw.handler, options.raw.handler, path, name).errorCheck()
            out.value?.toKString()
        }

        fun getMany(path: String, names: Collection<String>, flags: AttrCheckFlags): Map<String, String> =
            memoryScoped {
                val out = allocArray<CPointerVar<ByteVar>>(names.size)
                git_attr_get_many(
                    out.getPointer(this@memoryScoped),
                    raw.handler,
                    flags.value,
                    path,
                    names.size.convert(),
                    names.map { it.cstr.getPointer(this@memoryScoped) }.toCValues()
                ).errorCheck()
                val result = mutableMapOf<String, String>()
                for ((i, name) in names.withIndex()) {
                    result[name] = out[i]!!.toKString()
                }
                result
            }

        fun getManyExt(path: String, names: Collection<String>, options: AttrOptions): Map<String, String> =
            memoryScoped {
                val out = allocArray<CPointerVar<ByteVar>>(names.size)
                git_attr_get_many_ext(
                    out.getPointer(this@memoryScoped),
                    raw.handler,
                    options.raw.handler,
                    path,
                    names.size.convert(),
                    names.map { it.cstr.getPointer(this@memoryScoped) }.toCValues()
                ).errorCheck()
                val result = mutableMapOf<String, String>()
                for ((i, name) in names.withIndex()) {
                    result[name] = out[i]!!.toKString()
                }
                result
            }

        fun attrForeach(path: String, flags: AttrCheckFlags, callback: AttrForeachCallback) {
            val callbackPayload = object : AttrForeachCallbackPayload {
                override var attrForeachCallback: AttrForeachCallback? = callback
            }.asStableRef()
            git_attr_foreach(
                raw.handler,
                flags.value,
                path,
                staticAttrForeachCallback,
                callbackPayload.asCPointer()
            ).errorCheck()
            callbackPayload.dispose()
        }

        fun attrForeachExt(path: String, options: AttrOptions, callback: AttrForeachCallback) {
            val callbackPayload = object : AttrForeachCallbackPayload {
                override var attrForeachCallback: AttrForeachCallback? = callback
            }.asStableRef()
            git_attr_foreach_ext(
                raw.handler,
                options.raw.handler,
                path,
                staticAttrForeachCallback,
                callbackPayload.asCPointer()
            ).errorCheck()
            callbackPayload.dispose()
        }

        fun addMacro(name: String, values: String) {
            git_attr_add_macro(raw.handler, name, values).errorCheck()
        }

        fun cacheFlush() {
            git_attr_cache_flush(raw.handler).errorCheck()
        }
    }
}
