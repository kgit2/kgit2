package com.kgit2.common.error

import com.kgit2.exception.GitErrorCode
import com.kgit2.exception.GitError

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
