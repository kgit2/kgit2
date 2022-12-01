package com.kgit2.diff

import com.kgit2.annotations.Raw
import com.kgit2.common.error.GitErrorCode
import com.kgit2.common.memory.Memory
import com.kgit2.common.option.mutually.FileMode
import com.kgit2.memory.RawWrapper
import com.kgit2.oid.Oid
import kotlinx.cinterop.*
import libgit2.git_diff_file

@Raw(
    base = git_diff_file::class,
)
class DiffFile(raw: DiffFileRaw) : RawWrapper<git_diff_file, DiffFileRaw>(raw) {
    constructor(memory: Memory, handler: DiffFilePointer) : this(DiffFileRaw(memory, handler))

    val id: Oid = Oid(Memory(), raw.handler.pointed.id.ptr)

    val path: String? = raw.handler.pointed.path?.toKString()

    val size: ULong = raw.handler.pointed.size

    val flag: DiffFlag = DiffFlag(raw.handler.pointed.flags)

    val mod: FileMode = FileMode.fromRaw(raw.handler.pointed.mode.convert())

    val flags: DiffFlag = DiffFlag(raw.handler.pointed.flags)
}
