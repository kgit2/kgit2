package com.kgit2.model

import kotlinx.cinterop.*
import libgit2.git_buf
import libgit2.git_buf_dispose

fun CPointer<git_buf>.toKString(): String? = if (this.pointed.size > 0UL) {
    this.pointed.ptr?.readBytes(this.pointed.size.convert())?.decodeToString()
} else {
    null
}

fun <R> withGitBuf(content: String? = null, block: (CPointer<git_buf>) -> R): R {
    return memScoped {
        val buf = cValue<git_buf>().getPointer(this)
        if (content != null) {
            buf.pointed.ptr = content.cstr.getPointer(this)
            buf.pointed.size = content.length.convert()
        }
        val result = block(buf)
        git_buf_dispose(buf)
        result
    }
}

// interface IGitBuf : GitAutoFreeBase<CPointer<git_buf>> {
//     val ptr: String?
//     val size: ULong?
//     val reserved: ULong?
//     val disposed: Boolean
// }
//
// class GitBuf(
//     override val arena: Arena,
//     override val handler: CPointer<git_buf>,
// ) : IGitBuf {
//     companion object {
//         fun initialized(arena: Arena = Arena()): GitBuf {
//             val handler = cValue<git_buf>().getPointer(arena)
//             return GitBuf(arena, handler)
//         }
//     }
//
//     override val ptr: String?
//         get() = handler.pointed.ptr?.toKString()
//     override val size: ULong
//         get() = handler.pointed.size
//     override val reserved: ULong
//         get() = handler.pointed.reserved
//
//     override var disposed: Boolean = false
//
//     override fun free() {
//         git_buf_dispose(handler)
//         arena.clear()
//         disposed = true
//     }
// }
