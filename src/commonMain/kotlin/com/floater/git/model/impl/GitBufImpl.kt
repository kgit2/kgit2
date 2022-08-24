package com.floater.git.model.impl

import com.floater.git.model.GitBuf
import kotlinx.cinterop.Arena
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.cValue
import kotlinx.cinterop.pointed
import kotlinx.cinterop.toKString
import libgit2.git_buf
import libgit2.git_buf_dispose

class GitBufImpl (
    private val memScope: MemScope,
    override var handler: CPointer<git_buf>? = cValue<git_buf>().getPointer(memScope),
) : GitBuf {
    override val arena: Arena = Arena()

    override val ptr: String?
        get() = handler?.pointed?.ptr?.toKString()
    override val size: ULong?
        get() = handler?.pointed?.size
    override val reserved: ULong?
        get() = handler?.pointed?.reserved

    override var disposed: Boolean = false

    override fun dispose() {
        git_buf_dispose(handler)
        handler = null
        arena.clear()
        disposed = true
    }
}
