package com.floater.git.common

import com.floater.git.exception.GitException

fun Int.errorCheck() {
    if (this == 0) return
    throw GitException()
}
