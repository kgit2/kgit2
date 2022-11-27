package com.kgit2.worktree

import com.kgit2.annotations.Raw
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import kotlinx.cinterop.pointed
import libgit2.GIT_WORKTREE_PRUNE_OPTIONS_VERSION
import libgit2.git_worktree_prune_options
import libgit2.git_worktree_prune_options_init

@Raw(
    base = git_worktree_prune_options::class,
)
class WorktreePruneOptions(
    raw: WorktreePruneOptionsRaw = WorktreePruneOptionsRaw(initial =  {
        git_worktree_prune_options_init(this, GIT_WORKTREE_PRUNE_OPTIONS_VERSION).errorCheck()
    })
) : GitBase<git_worktree_prune_options, WorktreePruneOptionsRaw>(raw) {
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
