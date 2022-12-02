package com.kgit2.model

import com.kgit2.annotations.Raw
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.IterableBase
import com.kgit2.memory.RawWrapper
import kotlinx.cinterop.*
import libgit2.git_strarray
import libgit2.git_strarray_copy

@Raw(
    base = git_strarray::class,
    free = "git_strarray_free",
)
class StrArray (
    raw: StrarrayRaw = StrarrayRaw(initial = {}),
) : RawWrapper<git_strarray, StrarrayRaw>(raw), IterableBase<String?> {
    constructor(contents: Collection<String>? = null) : this(StrarrayRaw(initial = { memory ->
        if (contents != null) {
            this.pointed.strings = contents.map { it.cstr.getPointer(memory) }.toCValues().getPointer(memory)
            this.pointed.count = contents.size.convert()
        }
    }))

    fun copy(): StrArray = StrArray(StrarrayRaw(initial = {
        git_strarray_copy(this, raw.handler).errorCheck()
    }))

    override val size: Long
        get() = raw.handler.pointed.count.toLong()

    override fun get(index: Long): String? = raw.handler.pointed.strings?.get(index)?.toKString()
}

fun Collection<String>.toStrArray(): StrArray = StrArray(this)
