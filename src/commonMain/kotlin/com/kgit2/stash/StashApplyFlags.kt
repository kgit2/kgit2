package com.kgit2.stash

import libgit2.GIT_STASH_APPLY_DEFAULT
import libgit2.GIT_STASH_APPLY_REINSTATE_INDEX
import libgit2.git_stash_apply_flags

enum class StashApplyFlags(val value: git_stash_apply_flags) {
    Default(GIT_STASH_APPLY_DEFAULT),
    ReInstateIndex(GIT_STASH_APPLY_REINSTATE_INDEX),
    ;

    companion object {
        fun from(value: git_stash_apply_flags): StashApplyFlags {
            return when (value) {
                GIT_STASH_APPLY_DEFAULT -> Default
                GIT_STASH_APPLY_REINSTATE_INDEX -> ReInstateIndex
                else -> throw IllegalArgumentException("Unknown StashApplyFlags value: $value")
            }
        }
    }
}
