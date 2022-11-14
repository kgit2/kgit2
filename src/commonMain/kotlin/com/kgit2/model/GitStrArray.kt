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

// class StringArray(
//     handler: CPointer<git_strarray>,
//     override val arena: Arena,
//     private val strings: MutableList<String> = MutableList(handler.pointed.count.toInt()) { handler.pointed.strings!![it]!!.toKString() },
// ) : GitAutoFreeBase<CPointer<git_strarray>>, MutableList<String> by strings {
//     companion object {
//         fun initialized(arena: Arena = Arena()): StringArray {
//             val strings = mutableListOf<String>()
//             val handler = arena.alloc<git_strarray>()
//             return StringArray(handler.ptr, arena, strings)
//         }
//     }
//
//     override fun free() {
//         git_strarray_free(handler)
//         super.free()
//     }
//
//     override val handler: CPointer<git_strarray> = handler
//         get() {
//             field.pointed.strings = strings.toCStringArray(arena)
//             field.pointed.count = strings.size.convert()
//             return field
//         }
//         set(value) {
//             field = value
//             strings.clear()
//             strings.addAll(MutableList(value.pointed.count.toInt()) { value.pointed.strings!![it]!!.toKString() })
//         }
// }
//
// fun Collection<String>.toStringArray(arena: Arena = Arena()): StringArray =
//     StringArray(arena.alloc<git_strarray>().ptr, arena, toMutableList())
