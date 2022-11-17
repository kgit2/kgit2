package com.kgit2.repository

import cnames.structs.git_config
import cnames.structs.git_repository
import cnames.structs.git_submodule
import com.kgit2.common.error.errorCheck
import com.kgit2.common.error.toBoolean
import com.kgit2.common.error.toInt
import com.kgit2.config.Config
import com.kgit2.model.AutoFreeGitBase
import com.kgit2.model.toKString
import com.kgit2.model.withGitBuf
import com.kgit2.submodule.Submodule
import com.kgit2.worktree.Worktree
import kotlinx.cinterop.*
import libgit2.*

class Repository(
    override val handler: CPointer<git_repository>,
    override val arena: Arena,
) : AutoFreeGitBase<CPointer<git_repository>> {
    companion object {
        fun initial(path: String, bare: Boolean = false, options: RepositoryInitOptions? = null): Repository {
            assert(path.isNotEmpty())
            val arena = Arena()
            val handler = arena.allocPointerTo<git_repository>()
            git_repository_init(handler.ptr, path, bare.toInt().convert()).errorCheck()
            return Repository(handler.value!!, arena)
        }

        fun initialExt(path: String, opts: RepositoryInitOptions): Repository {
            assert(path.isNotEmpty())
            val arena = Arena()
            val handler = arena.allocPointerTo<git_repository>()
            git_repository_init_ext(handler.ptr, path, opts.toRaw(arena)).errorCheck()
            return Repository(handler.value!!, arena)
        }

        fun open(path: String, bare: Boolean = false): Repository {
            assert(path.isNotEmpty())
            val arena = Arena()
            val handler = arena.allocPointerTo<git_repository>()
            when (bare) {
                true -> git_repository_open_bare(handler.ptr, path).errorCheck()
                false -> git_repository_open(handler.ptr, path).errorCheck()
            }
            return Repository(handler.value!!, arena)
        }

        fun openExt(
            path: String,
            flags: RepositoryOpenFlag = RepositoryOpenFlag.OpenFromENV,
            ceilingDirs: String? = null,
        ): Repository {
            assert(path.isNotEmpty())
            val arena = Arena()
            val handler = arena.allocPointerTo<git_repository>()
            git_repository_open_ext(handler.ptr, path, flags.value, ceilingDirs).errorCheck()
            return Repository(handler.value!!, arena)
        }

        fun openFromWorktree(worktree: Worktree): Repository {
            val arena = Arena()
            val handler = arena.allocPointerTo<git_repository>()
            git_repository_open_from_worktree(handler.ptr, worktree.handler).errorCheck()
            return Repository(handler.value!!, arena)
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

    override fun free() {
        git_repository_free(handler)
        super.free()
    }

    val path: String?
        get() = git_repository_path(handler)?.toKString()

    val isBare: Boolean
        get() = git_repository_is_bare(handler).toBoolean()

    val isShallow: Boolean
        get() = git_repository_is_shallow(handler).toBoolean()

    val isWorkTree: Boolean
        get() = git_repository_is_worktree(handler).toBoolean()

    val isEmpty: Boolean
        get() = git_repository_is_empty(handler).toBoolean()

    val noteDefaultRef: String
        get() = withGitBuf { buf ->
            git_note_default_ref(buf, handler).errorCheck()
            buf.toKString()!!
        }

    fun config(): Config {
        val arena = Arena()
        val pointer = arena.allocPointerTo<git_config>()
        git_repository_config(pointer.ptr, handler).errorCheck()
        return Config(pointer.value!!, arena)
    }

    fun submodules(): List<Submodule> {
        val submodules = mutableListOf<Submodule>()
        val gitCallback: git_submodule_cb = staticCFunction { _, name, payload ->
            val (repository, sms) = payload!!.asStableRef<Pair<CPointer<git_repository>, MutableList<Submodule>>>()
                .get()
            val arena = Arena()
            val submodule = arena.allocPointerTo<git_submodule>()
            git_submodule_lookup(submodule.ptr, repository, name?.toKString()).errorCheck()
            sms.add(Submodule(submodule.value!!, arena))
            0
        }
        git_submodule_foreach(handler, gitCallback, StableRef.create(handler to submodules).asCPointer()).errorCheck()
        return submodules
    }
}
