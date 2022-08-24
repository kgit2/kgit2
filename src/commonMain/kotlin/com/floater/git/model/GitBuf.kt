package com.floater.git.model

import kotlinx.cinterop.CPointer
import libgit2.git_buf

interface GitBuf : GitBase<CPointer<git_buf>> {
    val ptr: String?
    val size: ULong?
    val reserved: ULong?
    val disposed: Boolean

    fun dispose()
}
