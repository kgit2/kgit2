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
