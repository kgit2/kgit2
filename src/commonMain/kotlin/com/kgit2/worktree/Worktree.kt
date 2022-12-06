package com.kgit2.worktree

import cnames.structs.git_worktree
import com.kgit2.annotations.Raw
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.RawWrapper
import com.kgit2.model.Buf
import com.kgit2.repository.Repository
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import libgit2.git_worktree_is_locked
import libgit2.git_worktree_is_prunable
import libgit2.git_worktree_lock
import libgit2.git_worktree_name
import libgit2.git_worktree_open_from_repository
import libgit2.git_worktree_path
import libgit2.git_worktree_prune
import libgit2.git_worktree_unlock
import libgit2.git_worktree_validate

@Raw(
    base = git_worktree::class,
    free = "git_worktree_free",
)
class Worktree(raw: WorktreeRaw) : RawWrapper<git_worktree, WorktreeRaw>(raw) {
    constructor(handler: WorktreePointer) : this(WorktreeRaw(Memory(), handler))

    constructor(
        secondaryInitial: WorktreeSecondaryInitial? = null,
    ) : this(WorktreeRaw(secondaryInitial = secondaryInitial))

    constructor(repository: Repository) : this(secondaryInitial = {
        git_worktree_open_from_repository(this.ptr, repository.raw.handler).errorCheck()
    })

    val name: String? = git_worktree_name(raw.handler)?.toKString()

    val path: String? = git_worktree_path(raw.handler)?.toKString()

    fun isLocked(): WorktreeStatus {
        var result: Int = -1
        val buf = Buf  {
            result = git_worktree_is_locked(this, raw.handler)
        }
        return when (result) {
            0 -> WorktreeStatus.Unlocked
            else -> WorktreeStatus.Locked(buf.buffer!!.toKString())
        }
    }

    fun validate() {
        git_worktree_validate(raw.handler).errorCheck()
    }

    fun lock(reason: String) {
        git_worktree_lock(raw.handler, reason).errorCheck()
    }

    fun unlock() {
        git_worktree_unlock(raw.handler).errorCheck()
    }

    fun prune(options: WorktreePruneOptions? = null) {
        git_worktree_prune(raw.handler, options?.raw?.handler).errorCheck()
    }

    fun isPruneAble(options: WorktreePruneOptions? = null) {
        git_worktree_is_prunable(raw.handler, options?.raw?.handler).errorCheck()
    }
}
