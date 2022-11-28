package com.kgit2.worktree

import com.kgit2.annotations.FlagMask
import libgit2.git_worktree_prune_t

@FlagMask(
    flagsType = git_worktree_prune_t::class,
    "GIT_WORKTREE_PRUNE_VALID",
    "GIT_WORKTREE_PRUNE_WORKING_TREE",
    "GIT_WORKTREE_PRUNE_LOCKED",
)
data class WorktreePruneOptionsFlag(
    override var flags: UInt,
    override val onFlagsChanged: ((UInt) -> Unit)? = null,
) : WorktreePruneOptionsFlagMask<WorktreePruneOptionsFlag>
