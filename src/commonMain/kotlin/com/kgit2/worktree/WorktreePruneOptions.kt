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

    val flags: WorktreePruneOptionsFlag = WorktreePruneOptionsFlag(raw.handler.pointed.flags) {
        raw.handler.pointed.flags = it
    }
}
