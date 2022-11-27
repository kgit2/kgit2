package com.kgit2.common.error

import kotlinx.cinterop.pointed
import kotlinx.cinterop.toKString
import libgit2.giterr_last

open class GitError(
    open val code: GitErrorCode,
) : Exception(run {
    val err = giterr_last()
    code.toString() + if (err != null) " : klass(${err.pointed.klass}) : ${err.pointed.message!!.toKString()}" else ""
})
