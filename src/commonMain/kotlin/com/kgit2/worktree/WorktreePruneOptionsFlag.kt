package com.kgit2.worktree

import com.kgit2.annotations.FlagMask
import com.kgit2.common.option.BaseMultiple
import kotlinx.cinterop.convert
import libgit2.GIT_WORKTREE_PRUNE_LOCKED
import libgit2.GIT_WORKTREE_PRUNE_VALID
import libgit2.GIT_WORKTREE_PRUNE_WORKING_TREE
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
