package com.kgit2.checkout

import libgit2.GIT_RESET_HARD
import libgit2.GIT_RESET_MIXED
import libgit2.GIT_RESET_SOFT
import libgit2.git_reset_t

enum class ResetType(val value: git_reset_t) {
    Soft(GIT_RESET_SOFT),
    Mixed(GIT_RESET_MIXED),
    Hard(GIT_RESET_HARD),
    ;

    companion object {
        fun fromRaw(value: git_reset_t): ResetType {
            return when (value) {
                GIT_RESET_SOFT -> Soft
                GIT_RESET_MIXED -> Mixed
                GIT_RESET_HARD -> Hard
                else -> throw IllegalArgumentException("Unknown value: $value")
            }
        }
    }
}
