package com.kgit2.worktree

import com.kgit2.common.option.BaseMultiple
import libgit2.GIT_WORKTREE_PRUNE_LOCKED
import libgit2.GIT_WORKTREE_PRUNE_VALID
import libgit2.GIT_WORKTREE_PRUNE_WORKING_TREE
import libgit2.git_worktree_prune_t

data class WorktreePruneOptionsFlag(val value: git_worktree_prune_t) : BaseMultiple<WorktreePruneOptionsFlag>() {
    companion object {
        val Valid = WorktreePruneOptionsFlag(GIT_WORKTREE_PRUNE_VALID)
        val Locked = WorktreePruneOptionsFlag(GIT_WORKTREE_PRUNE_LOCKED)
        val WorkingTree = WorktreePruneOptionsFlag(GIT_WORKTREE_PRUNE_WORKING_TREE)
    }

    override val longValue: ULong
        get() = value.toULong()
}
