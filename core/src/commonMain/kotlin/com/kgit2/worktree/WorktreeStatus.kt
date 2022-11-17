package com.kgit2.worktree

sealed class WorktreeStatus {
    object Unlocked : WorktreeStatus()
    data class Locked(val reason: String?) : WorktreeStatus()
}
