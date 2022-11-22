package com.kgit2.common.error

import com.kgit2.exception.GitErrorCode
import com.kgit2.exception.GitException

fun Int.errorCheck() {
    if (this == 0) return
    throw GitException(runCatching { GitErrorCode.fromRaw(this) }.getOrNull())
}


fun Int.toBoolean(): Boolean {
    return this != 0
}

fun Boolean.toInt(): Int {
    return if (this) 1 else 0
}
