package com.kgit2.worktree

import cnames.structs.git_worktree
import com.kgit2.common.error.errorCheck
import com.kgit2.model.AutoFreeGitBase
import com.kgit2.model.toKString
import com.kgit2.model.withGitBuf
import com.kgit2.repository.Repository
import kotlinx.cinterop.*
import libgit2.*

class Worktree(
    override val handler: CPointer<git_worktree>,
    override val arena: Arena,
) : AutoFreeGitBase<CPointer<git_worktree>> {
    companion object {
        fun openFromRepository(repository: Repository): Worktree {
            val arena = Arena()
            val handler = arena.allocPointerTo<git_worktree>()
            git_worktree_open_from_repository(handler.ptr, repository.handler).errorCheck()
            return Worktree(handler.value!!, arena)
        }
    }

    override fun free() {
        git_worktree_free(handler)
        super.free()
    }

    val name: String?
        get() = git_worktree_name(handler)?.toKString()

    val path: String?
        get() = git_worktree_path(handler)?.toKString()

    val isLocked: WorktreeStatus
        get() = withGitBuf {
            when (git_worktree_is_locked(it, handler)) {
                0 -> WorktreeStatus.Unlocked
                else -> WorktreeStatus.Locked(it.toKString())
            }
        }

    fun validate() {
        git_worktree_validate(handler).errorCheck()
    }

    fun lock(reason: String) {
        git_worktree_lock(handler, reason).errorCheck()
    }

    fun unlock() {
        git_worktree_unlock(handler).errorCheck()
    }

    fun prune(options: WorktreePruneOptions? = null) {
        git_worktree_prune(handler, options?.handler).errorCheck()
    }

    fun isPruneAble(options: WorktreePruneOptions? = null) {
        git_worktree_is_prunable(handler, options?.handler).errorCheck()
    }
}
