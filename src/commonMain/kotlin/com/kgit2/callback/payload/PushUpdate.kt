package com.kgit2.callback.payload

import com.kgit2.oid.Oid

data class PushUpdate(
    val srcRefName: String,
    val dstRefName: String,
    val src: Oid,
    val dst: Oid,
)
