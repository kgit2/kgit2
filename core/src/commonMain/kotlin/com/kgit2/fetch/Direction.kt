package com.kgit2.fetch

import libgit2.GIT_DIRECTION_FETCH
import libgit2.GIT_DIRECTION_PUSH
import libgit2.git_direction

enum class Direction(val value: git_direction) {
    FETCH(GIT_DIRECTION_FETCH),
    PUSH(GIT_DIRECTION_PUSH);

    companion object {
        fun fromRaw(value: git_direction): Direction {
            return when (value) {
                GIT_DIRECTION_FETCH -> FETCH
                GIT_DIRECTION_PUSH -> PUSH
                else -> throw IllegalArgumentException("Unknown value: $value")
            }
        }
    }
}
