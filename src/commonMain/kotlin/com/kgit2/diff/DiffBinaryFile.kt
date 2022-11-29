package com.kgit2.diff

import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import kotlinx.cinterop.*
import libgit2.git_diff_binary_file

@Raw(
    base = git_diff_binary_file::class,
)
class DiffBinaryFile(raw: DiffBinaryFileRaw) : GitBase<git_diff_binary_file, DiffBinaryFileRaw>(raw) {
    constructor(
        memory: Memory = Memory(),
        handler: DiffBinaryFilePointer = memory.alloc<git_diff_binary_file>().ptr,
    ) : this(DiffBinaryFileRaw(memory, handler))

    val fileType: DiffBinaryFileType = DiffBinaryFileType.from(raw.handler.pointed.type)

    val data: ByteArray? = raw.handler.pointed.data?.readBytes(raw.handler.pointed.datalen.convert() ?: 0)

    val inflatedLen: ULong = raw.handler.pointed.inflatedlen
}
