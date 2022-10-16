package com.floater.git.model

import kotlinx.cinterop.*
import libgit2.git_buf
import libgit2.git_buf_dispose

interface IGitBuf : GitBase<CPointer<git_buf>> {
    val ptr: String?
    val size: ULong?
    val reserved: ULong?
    val disposed: Boolean

    fun dispose()
}

class GitBuf(
    private val memScope: MemScope,
    override var handler: CPointer<git_buf> = cValue<git_buf>().getPointer(memScope),
) : IGitBuf {
    override val arena: Arena = Arena()

    override val ptr: String?
        get() = handler.pointed.ptr?.toKString()
    override val size: ULong
        get() = handler.pointed.size
    override val reserved: ULong
        get() = handler.pointed.reserved

    override var disposed: Boolean = false

    override fun dispose() {
        git_buf_dispose(handler)
        arena.clear()
        disposed = true
    }
}
