package com.kgit2.diff

import com.kgit2.annotations.Raw
import com.kgit2.common.error.GitErrorCode
import com.kgit2.common.extend.toBoolean
import com.kgit2.common.memory.Memory
import com.kgit2.memory.RawWrapper
import kotlinx.cinterop.convert
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import libgit2.git_diff_binary

@Raw(
    base = git_diff_binary::class,
)
class DiffBinary(raw: DiffBinaryRaw) : RawWrapper<git_diff_binary, DiffBinaryRaw>(raw) {
    constructor(memory: Memory = Memory(), handler: DiffBinaryPointer) : this(DiffBinaryRaw(memory, handler))

    val containsData: Boolean = raw.handler.pointed.contains_data.convert<Int>().toBoolean()

    val oldFile: DiffBinaryFile = DiffBinaryFile(handler = raw.handler.pointed.old_file.ptr)

    val newFile: DiffBinaryFile = DiffBinaryFile(handler = raw.handler.pointed.new_file.ptr)
}
