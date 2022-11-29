package com.kgit2.diff

import libgit2.git_diff_binary_t

enum class DiffBinaryFileType(val value: git_diff_binary_t) {
    None(git_diff_binary_t.GIT_DIFF_BINARY_NONE),
    Literal(git_diff_binary_t.GIT_DIFF_BINARY_LITERAL),
    Delta(git_diff_binary_t.GIT_DIFF_BINARY_DELTA),
    ;

    companion object {
        fun from(value: git_diff_binary_t): DiffBinaryFileType {
            return when (value) {
                git_diff_binary_t.GIT_DIFF_BINARY_NONE -> None
                git_diff_binary_t.GIT_DIFF_BINARY_LITERAL -> Literal
                git_diff_binary_t.GIT_DIFF_BINARY_DELTA -> Delta
                else -> throw IllegalArgumentException("Unknown value: $value")
            }
        }
    }
}
