package com.floater.git.common.error

import com.floater.git.exception.GitException

fun Int.errorCheck() {
    if (this == 0) return
    throw GitException()
}
