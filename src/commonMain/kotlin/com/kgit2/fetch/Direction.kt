package com.kgit2.fetch

import libgit2.GIT_DIRECTION_FETCH
import libgit2.GIT_DIRECTION_PUSH
import libgit2.git_direction

enum class Direction(val value: git_direction) {
    Fetch(GIT_DIRECTION_FETCH),
    Push(GIT_DIRECTION_PUSH),
    ;

    companion object {
        fun fromRaw(value: git_direction): Direction {
            return when (value) {
                GIT_DIRECTION_FETCH -> Fetch
                GIT_DIRECTION_PUSH -> Push
                else -> throw IllegalArgumentException("Unknown value: $value")
            }
        }
    }
}
