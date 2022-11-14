package com.kgit2.callback.payload

import com.kgit2.model.Oid

data class PushUpdate(
    val srcRefname: String,
    val dstRefname: String,
    val src: Oid,
    val dst: Oid,
)
