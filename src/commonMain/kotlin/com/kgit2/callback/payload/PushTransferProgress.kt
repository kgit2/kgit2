package com.kgit2.callback.payload

interface PushTransferProgress {
    fun pushTransferProgress(
        current: Int,
        total: Int,
        bytes: Int,
    )
}
