package com.kgit2.branch

import libgit2.GIT_BRANCH_ALL
import libgit2.GIT_BRANCH_LOCAL
import libgit2.GIT_BRANCH_REMOTE
import libgit2.git_branch_t

enum class BranchType(val value: git_branch_t) {
    Local(GIT_BRANCH_LOCAL),
    Remote(GIT_BRANCH_REMOTE),
    All(GIT_BRANCH_ALL),
    ;

    companion object {
        fun fromRaw(value: git_branch_t): BranchType {
            return when (value) {
                GIT_BRANCH_LOCAL -> Local
                GIT_BRANCH_REMOTE -> Remote
                GIT_BRANCH_ALL -> All
                else -> throw IllegalArgumentException("Unknown branch type: $value")
            }
        }
    }
}
