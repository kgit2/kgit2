package com.floater.git.exception

import kotlinx.cinterop.pointed
import kotlinx.cinterop.toKString
import libgit2.giterr_last

class GitException : Exception(run {
    val err = giterr_last()
    err!!.pointed.message!!.toKString()
})
