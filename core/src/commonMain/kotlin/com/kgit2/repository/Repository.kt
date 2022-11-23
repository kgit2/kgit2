@file:Suppress("PropertyName", "MemberVisibilityCanBePrivate")

package com.kgit2.repository

import cnames.structs.git_repository
import com.kgit2.branch.Branch
import com.kgit2.branch.BranchIterator
import com.kgit2.branch.BranchType
import com.kgit2.checkout.CheckoutOptions
import com.kgit2.checkout.ResetType
import com.kgit2.commit.AnnotatedCommit
import com.kgit2.commit.Commit
import com.kgit2.common.error.errorCheck
import com.kgit2.common.error.toBoolean
import com.kgit2.common.error.toInt
import com.kgit2.common.memory.Memory
import com.kgit2.common.memory.memoryScoped
import com.kgit2.config.Config
import com.kgit2.exception.GitError
import com.kgit2.exception.GitErrorCode
import com.kgit2.memory.GitBase
import com.kgit2.memory.Raw
import com.kgit2.model.*
import com.kgit2.`object`.Object
import com.kgit2.odb.Odb
import com.kgit2.reference.Reference
import com.kgit2.reference.ReferenceIterator
import com.kgit2.remote.Remote
import com.kgit2.status.Status
import com.kgit2.status.StatusList
import com.kgit2.status.StatusOptions
import com.kgit2.submodule.Submodule
import com.kgit2.worktree.Worktree
import kotlinx.cinterop.*
import libgit2.*

typealias RepositoryPointer = CPointer<git_repository>

typealias RepositorySecondaryPointer = CPointerVar<git_repository>

typealias RepositoryInitial = RepositorySecondaryPointer.(Memory) -> Unit

class RepositoryRaw(
    memory: Memory,
    handler: RepositoryPointer,
) : Raw<git_repository>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: RepositorySecondaryPointer = memory.allocPointerTo(),
        initial: RepositoryInitial? = null,
    ) : this(memory, handler.apply {
        runCatching {
            initial?.invoke(handler, memory)
        }.onFailure {
            git_repository_free(handler.value!!)
            memory.free()
        }.getOrThrow()
    }.value!!)

    override val beforeFree: () -> Unit = {
        git_repository_free(handler)
    }
}

class Repository(raw: RepositoryRaw) : GitBase<git_repository, RepositoryRaw>(raw) {
    constructor(memory: Memory, handler: RepositoryPointer) : this(RepositoryRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: RepositorySecondaryPointer = memory.allocPointerTo(),
        initial: RepositoryInitial? = null,
    ) : this(RepositoryRaw(memory, handler, initial))

    companion object {
        fun initial(path: String, bare: Boolean = false): Repository = Repository() {
            git_repository_init(this.ptr, path, bare.toInt().convert()).errorCheck()
        }

        fun initialExt(path: String, options: RepositoryInitOptions): Repository = Repository() {
            git_repository_init_ext(this.ptr, path, options.raw.handler).errorCheck()
        }

        fun open(path: String, bare: Boolean = false): Repository = Repository() {
            when (bare) {
                true -> git_repository_open_bare(this.ptr, path)
                false -> git_repository_open(this.ptr, path)
            }.errorCheck()
        }

        fun openExt(
            path: String,
            flags: RepositoryOpenFlags = RepositoryOpenFlags.OpenFromENV,
            ceilingDirs: String? = null,
        ): Repository = Repository() {
            git_repository_open_ext(this.ptr, path, flags.value, ceilingDirs).errorCheck()
        }

        fun openFromWorktree(worktree: Worktree): Repository = Repository() {
            git_repository_open_from_worktree(this.ptr, worktree.raw.handler).errorCheck()
        }

        fun openFromEnv(): Repository = Repository() {
            git_repository_open_ext(this.ptr, null, RepositoryOpenFlags.OpenFromENV.value, null).errorCheck()
        }

        fun discover(path: String): Repository {
            val discoverPath = withGitBuf { buf ->
                git_repository_discover(buf, path, 1, null).errorCheck()
                buf.toKString()!!
            }
            return open(discoverPath)
        }

        fun clone(url: String, localPath: String, cloneOptions: CloneOptions): Repository = Repository() {
            git_clone(this.ptr, url, localPath, cloneOptions.raw.handler).errorCheck()
        }

        fun cloneRecursive(url: String, localPath: String, cloneOptions: CloneOptions): Repository {
            val repository = Repository() {
                git_clone(this.ptr, url, localPath, cloneOptions.raw.handler).errorCheck()
            }
            repository.Submodule.updateSubmodules()
            return repository
        }

        fun fromOdb(odb: Odb): Repository = Repository() {
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

    fun config(): Config = Config() {
        git_repository_config(this.ptr, raw.handler).errorCheck()
    }

    val Remote = RemoteModule()

    inner class RemoteModule {
        fun remoteList(): List<String> = withGitStrArray {
            git_remote_list(it, raw.handler).errorCheck()
            it.toList()
        }

        fun findRemote(name: String): Remote = Remote() {
            git_remote_lookup(this.ptr, raw.handler, name).errorCheck()
        }

        fun remote(name: String, url: String): Remote = Remote() {
            git_remote_create(this.ptr, raw.handler, name, url).errorCheck()
        }

        fun remoteWithFetch(name: String, url: String, fetch: String): Remote = Remote() {
            git_remote_create_with_fetchspec(this.ptr, raw.handler, name, url, fetch).errorCheck()
        }

        fun remoteAnonymous(url: String): Remote = Remote() {
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

        fun head(): Reference = Reference() {
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
    }

    val Reference = ReferenceModule()

    inner class ReferenceModule {
        fun references(): ReferenceIterator = ReferenceIterator() {
            git_reference_iterator_new(this.ptr, raw.handler).errorCheck()
        }

        fun globReferences(glob: String): ReferenceIterator = ReferenceIterator() {
            git_reference_iterator_glob_new(this.ptr, raw.handler, glob).errorCheck()
        }
    }

    val Branch = BranchModule()

    inner class BranchModule {
        fun createBranch(branchName: String, target: Commit, force: Boolean = false): Branch = Branch() {
            git_branch_create(this.ptr, raw.handler, branchName, target.raw.handler, force.toInt()).errorCheck()
        }

        fun createBranchFromAnnotated(
            branchName: String,
            annotatedCommit: AnnotatedCommit,
            force: Boolean = false,
        ): Branch = Branch() {
            git_branch_create_from_annotated(
                this.ptr,
                raw.handler,
                branchName,
                annotatedCommit.raw.handler,
                force.toInt()
            ).errorCheck()
        }

        fun branches(listType: BranchType): BranchIterator = BranchIterator() {
            git_branch_iterator_new(this.ptr, raw.handler, listType.value).errorCheck()
        }

        fun findBranch(name: String, branchType: BranchType): Branch = Branch() {
            git_branch_lookup(this.ptr, raw.handler, name, branchType.value).errorCheck()
        }
    }

    val Status = StatusModule()

    inner class StatusModule {
        fun statusList(options: StatusOptions): StatusList = StatusList() {
            git_status_list_new(this.ptr, raw.handler, options.raw.handler).errorCheck()
        }

        fun statusShouldIgnore(path: String): Boolean = memoryScoped {
            val ignored = alloc<IntVar>()
            git_status_should_ignore(ignored.ptr, raw.handler, path).toBoolean()
            ignored.value.toBoolean()
        }

        fun statusFile(path: String): Status = memoryScoped {
            val statusFlags = alloc<git_status_tVar>()
            git_status_file(statusFlags.ptr, raw.handler, path).errorCheck()
            Status(statusFlags.value)
        }
    }

    val Submodule = SubmoduleModule()

    inner class SubmoduleModule {
        fun submodules(): List<Submodule> {
            val gitCallback: git_submodule_cb = staticCFunction { _, name, payload ->
                val (repository, submodules) = payload!!.asStableRef<Pair<RepositoryPointer, MutableList<Submodule>>>()
                    .get()
                submodules.add(Submodule() {
                    git_submodule_lookup(this.ptr, repository, name?.toKString()).errorCheck()
                })
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
    }

    val Rev = RevModule()

    inner class RevModule {
        fun revParse(spec: String): RevSpec = RevSpec() {
            git_revparse(this, raw.handler, spec).errorCheck()
        }

        fun revParseSingle(spec: String) = Object() {
            git_revparse_single(this.ptr, raw.handler, spec).errorCheck()
        }

        fun revParseExt(spec: String): Pair<Object, Reference> {
            lateinit var reference: Reference
            val `object` = Object() {
                reference = Reference() {
                    git_revparse_ext(this@Object.ptr, this@Reference.ptr, raw.handler, spec).errorCheck()
                }
            }
            return `object` to reference
        }
    }
}
