package com.kgit2.stash

import libgit2.GIT_STASH_DEFAULT
import libgit2.GIT_STASH_INCLUDE_IGNORED
import libgit2.GIT_STASH_INCLUDE_UNTRACKED
import libgit2.GIT_STASH_KEEP_INDEX
import libgit2.git_stash_flags

enum class StashFlags(val value: git_stash_flags) {
    /**
     * No option, default
     */
    Default(GIT_STASH_DEFAULT),

    /**
     * All changes already added to the index are left intact in the working directory
     */
    KeepIndex(GIT_STASH_KEEP_INDEX),

    /**
     * All untracked files are also stashed and then cleaned up from the working directory
     */
    IncludeUntracked(GIT_STASH_INCLUDE_UNTRACKED),

    /**
     * All ignored files are also stashed and then cleaned up from the working directory
     */
    IncludeIgnored(GIT_STASH_INCLUDE_IGNORED),
    ;

    companion object {
        fun from(value: git_stash_flags): StashFlags {
            return when (value) {
                GIT_STASH_DEFAULT -> Default
                GIT_STASH_KEEP_INDEX -> KeepIndex
                GIT_STASH_INCLUDE_UNTRACKED -> IncludeUntracked
                GIT_STASH_INCLUDE_IGNORED -> IncludeIgnored
                else -> throw IllegalArgumentException("Unknown StashFlags value: $value")
            }
        }
    }
}
