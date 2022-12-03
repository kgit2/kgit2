package com.kgit2.model

import com.kgit2.annotations.Raw
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.MutableListBase
import com.kgit2.memory.RawWrapper
import kotlinx.cinterop.convert
import kotlinx.cinterop.cstr
import kotlinx.cinterop.pointed
import kotlinx.cinterop.toCValues
import libgit2.git_strarray
import libgit2.git_strarray_copy
import libgit2.git_strarray_dispose

@Raw(
    base = git_strarray::class,
    free = "git_strarray_free",
)
class StrArray(
    raw: StrarrayRaw = StrarrayRaw(initial = {}),
    override val innerList: MutableList<String> = mutableListOf(),
) : RawWrapper<git_strarray, StrarrayRaw>(raw),
    MutableListBase<String> {
    constructor(memory: Memory, struct: git_strarray) : this(StrarrayRaw(memory, struct))

    constructor(contents: Collection<String>? = null) : this(StrarrayRaw(initial = {})) {
        contents?.let { addAll(it) }
    }

    fun copy(): StrArray = StrArray(StrarrayRaw(initial = {
        git_strarray_copy(this, raw.handler).errorCheck()
    }))

    override fun updateRaw(list: List<String>) {
        raw.handler.pointed.strings = list.map { it.cstr.getPointer(raw.memory) }.toCValues().getPointer(raw.memory)
        raw.handler.pointed.count = list.size.convert()
    }

    override fun clearRaw() {
        git_strarray_dispose(raw.handler)
    }
}

fun Collection<String>.toStrArray(): StrArray = StrArray(this)
