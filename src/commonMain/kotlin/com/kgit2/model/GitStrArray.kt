package com.kgit2.model

import kotlinx.cinterop.*
import libgit2.git_strarray
import libgit2.git_strarray_free

fun CPointer<git_strarray>.toList(): List<String> =
    List(this.pointed.count.toInt()) { this.pointed.strings!![it]!!.toKString() }

fun <R> withGitStrArray(content: Collection<String>? = null, block: (CPointer<git_strarray>) -> R): R {
    return memScoped {
        val array = alloc<git_strarray>()
        if (content != null) {
            array.count = content.size.convert()
            array.strings = content.map { it.cstr.ptr }.toCValues().ptr
        }
        val result = block(array.ptr)
        git_strarray_free(array.ptr)
        result
    }
}
