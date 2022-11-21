package com.kgit2.repository

import cnames.structs.git_repository
import com.kgit2.common.error.errorCheck
import com.kgit2.common.error.toBoolean
import com.kgit2.common.error.toInt
import com.kgit2.common.memory.Memory
import com.kgit2.config.Config
import com.kgit2.memory.Raw
import com.kgit2.memory.GitBase
import com.kgit2.model.toKString
import com.kgit2.model.withGitBuf
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
            flags: RepositoryOpenFlag = RepositoryOpenFlag.OpenFromENV,
            ceilingDirs: String? = null,
        ): Repository = Repository() {
            git_repository_open_ext(this.ptr, path, flags.value, ceilingDirs).errorCheck()
        }

        fun openFromWorktree(worktree: Worktree): Repository = Repository() {
            git_repository_open_from_worktree(this.ptr, worktree.raw.handler).errorCheck()
        }

        fun discover(path: String): Repository {
            val discoverPath = withGitBuf { buf ->
                git_repository_discover(buf, path, 1, null).errorCheck()
                buf.toKString()!!
            }
            return open(discoverPath)
        }

        // TODO()
        // fun clone(url: String, localPath: String, bare: Boolean = false): Repository {
        // }
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

    fun config(): Config = Config() {
        git_repository_config(this.ptr, raw.handler).errorCheck()
    }

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
        git_submodule_foreach(raw.handler, gitCallback, StableRef.create(raw.handler to submodules).asCPointer()).errorCheck()
        return submodules
    }
}
