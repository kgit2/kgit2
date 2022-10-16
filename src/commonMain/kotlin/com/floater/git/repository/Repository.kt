package com.floater.git.repository

import cnames.structs.git_config
import cnames.structs.git_repository
import com.floater.git.common.option.GitRepositoryOpenFlag
import com.floater.git.common.option.RepositoryInitOptions
import com.floater.git.common.error.errorCheck
import com.floater.git.config.Config
import com.floater.git.exception.GitException
import com.floater.git.model.GitBase
import com.floater.git.model.GitBuf
import kotlinx.cinterop.*
import libgit2.*

open class Repository(
    override var handler: CPointer<git_repository>,
    open var path: String,
) : GitBase<CPointer<git_repository>> {
    override val arena: Arena = Arena()

    open fun free() {
        git_repository_free(handler)
        arena.clear()
    }

    open fun config(): Config {
        val config = Config()
        config.handler = memScoped {
            val pointer = allocPointerTo<git_config>()
            git_repository_config(pointer.ptr, handler).errorCheck()
            pointer.value!!
        }
        return config
    }

    open fun isBare(): Boolean {
        return git_repository_is_bare(handler) == 1
    }

    open fun isShallow(): Boolean {
        return git_repository_is_shallow(handler) == 1
    }

    open fun isWorktree(): Boolean {
        return git_repository_is_worktree(handler) == 1
    }

    /// Tests whether this repository is empty.
    open fun isEmpty(): Boolean {
        return when (git_repository_is_empty(handler)) {
            1 -> true
            0 -> false
            else -> throw GitException()
        }
    }

    /// Returns the path to the `.git` folder for normal repositories or the
    /// repository itself for bare repositories.
    open fun path(): String? {
        return git_repository_path(handler)?.toKString()
    }

    open fun noteDefaultRef(): String {
        lateinit var result: String
        memScoped {
            val buf = GitBuf(this@memScoped)
            git_note_default_ref(buf.handler, handler).errorCheck()
            result = buf.ptr!!
            buf.dispose()
        }
        return result
    }
}
