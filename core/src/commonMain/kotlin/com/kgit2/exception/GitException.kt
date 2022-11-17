package com.kgit2.exception

import kotlinx.cinterop.pointed
import kotlinx.cinterop.toKString
import libgit2.giterr_last

open class GitException(
    open val errorCode: GitErrorCode? = null,
) : Exception(run {
    val err = giterr_last()!!
    errorCode?.let { "${it.name} " } + "${err.pointed.klass}: ${err.pointed.message!!.toKString()}"
})
