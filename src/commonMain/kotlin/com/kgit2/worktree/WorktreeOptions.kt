package com.kgit2.worktree

import com.kgit2.common.error.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import com.kgit2.memory.Raw
import kotlinx.cinterop.*
import libgit2.GIT_WORKTREE_PRUNE_OPTIONS_VERSION
import libgit2.git_worktree_prune_options
import libgit2.git_worktree_prune_options_init

typealias WorktreePruneOptionsPointer = CPointer<git_worktree_prune_options>

typealias WorktreePruneOptionsSecondaryPointer = CPointerVar<git_worktree_prune_options>

typealias WorktreePruneOptionsInitial = WorktreePruneOptionsSecondaryPointer.(Memory) -> Unit

class WorktreePruneOptionsRaw(
    memory: Memory = Memory(),
    handler: WorktreePruneOptionsPointer = memory.alloc<git_worktree_prune_options>().ptr,
) : Raw<git_worktree_prune_options>(memory, handler) {
    init {
        git_worktree_prune_options_init(handler, GIT_WORKTREE_PRUNE_OPTIONS_VERSION).errorCheck()
    }
    // constructor(
    //     memory: Memory = Memory(),
    //     handler: WorktreePruneOptionsSecondaryPointer = memory.allocPointerTo(),
    //     initial: WorktreePruneOptionsInitial? = null,
    // ) : this(memory, handler.apply {
    //     runCatching {
    //         initial?.invoke(handler, memory)
    //     }.onFailure {
    //         memory.free()
    //     }.getOrThrow()
    // }.value!!)
}

class WorktreePruneOptions(raw: WorktreePruneOptionsRaw) :
    GitBase<git_worktree_prune_options, WorktreePruneOptionsRaw>(raw) {
    constructor(memory: Memory, handler: WorktreePruneOptionsPointer) : this(WorktreePruneOptionsRaw(memory, handler))

    private fun flag(flag: WorktreePruneOptionsFlag, on: Boolean): WorktreePruneOptions {
        when (on) {
            true -> raw.handler.pointed.flags = raw.handler.pointed.flags or flag.value
            false -> raw.handler.pointed.flags = raw.handler.pointed.flags and flag.value.inv()
        }
        return this
    }

    fun valid(valid: Boolean): WorktreePruneOptions = flag(WorktreePruneOptionsFlag.Valid, valid)

    fun locked(locked: Boolean): WorktreePruneOptions = flag(WorktreePruneOptionsFlag.Locked, locked)

    fun workingTree(workingTree: Boolean): WorktreePruneOptions =
        flag(WorktreePruneOptionsFlag.WorkingTree, workingTree)
}
