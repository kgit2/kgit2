package com.kgit2.worktree

import cnames.structs.git_worktree
import com.kgit2.common.error.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.Binding
import com.kgit2.memory.GitBase
import com.kgit2.model.toKString
import com.kgit2.model.withGitBuf
import com.kgit2.repository.Repository
import kotlinx.cinterop.*
import libgit2.*

typealias WorktreePointer = CPointer<git_worktree>

typealias WorktreeSecondaryPointer = CPointerVar<git_worktree>

typealias WorktreeInitial = WorktreeSecondaryPointer.(Memory) -> Unit

class WorktreeRaw(
    memory: Memory,
    handler: WorktreePointer,
) : Binding<git_worktree>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: WorktreeSecondaryPointer = memory.allocPointerTo(),
        initial: WorktreeInitial? = null,
    ) : this(memory, handler.apply {
        runCatching {
            initial?.invoke(handler, memory)
        }.onFailure {
            git_worktree_free(handler.value!!)
            memory.free()
        }.getOrThrow()
    }.value!!)

    override val beforeFree: () -> Unit = {
        git_worktree_free(handler)
    }
}

class Worktree(raw: WorktreeRaw) : GitBase<git_worktree, WorktreeRaw>(raw) {
    constructor(memory: Memory, handler: WorktreePointer) : this(WorktreeRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: WorktreeSecondaryPointer = memory.allocPointerTo(),
        initial: WorktreeInitial? = null,
    ) : this(WorktreeRaw(memory, handler, initial))

    constructor(repository: Repository) : this(initial = {
        git_worktree_open_from_repository(this.ptr, repository.raw.handler).errorCheck()
    })

    val name: String?
        get() = git_worktree_name(raw.handler)?.toKString()

    val path: String?
        get() = git_worktree_path(raw.handler)?.toKString()

    val isLocked: WorktreeStatus
        get() = withGitBuf {
            when (git_worktree_is_locked(it, raw.handler)) {
                0 -> WorktreeStatus.Unlocked
                else -> WorktreeStatus.Locked(it.toKString())
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
