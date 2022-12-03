package com.kgit2.common.extend

import com.kgit2.common.error.GitError
import com.kgit2.common.error.GitErrorCode

@Throws(GitError::class)
fun Int.errorCheck() {
    if (this == 0) return
    throw GitError(GitErrorCode.fromRaw(this))
}

fun Int.toBoolean(): Boolean {
    return this != 0
}

fun Boolean.toInt(): Int {
    return if (this) 1 else 0
}
