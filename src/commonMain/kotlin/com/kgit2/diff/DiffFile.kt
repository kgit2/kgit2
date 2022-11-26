package com.kgit2.diff

import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.common.option.mutually.FileMode
import com.kgit2.memory.GitBase
import com.kgit2.model.Oid
import kotlinx.cinterop.convert
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import libgit2.git_diff_file

@Raw(
    base = git_diff_file::class,
)
class DiffFile(raw: DiffFileRaw) : GitBase<git_diff_file, DiffFileRaw>(raw) {
    constructor(memory: Memory, handler: DiffFilePointer) : this(DiffFileRaw(memory, handler))

    val id: Oid = Oid(Memory(), raw.handler.pointed.id.ptr)

    val path: String? = raw.handler.pointed.path?.toKString()

    val size: ULong = raw.handler.pointed.size

    val flag: DiffFlag = DiffFlag(raw.handler.pointed.flags)

    val mod: FileMode = FileMode.fromRaw(raw.handler.pointed.mode.convert())

    val isBinary: Boolean = flag in DiffFlag.Binary

    val isNotBinary: Boolean = flag in DiffFlag.NotBinary

    val isValidId: Boolean = flag in DiffFlag.ValidID

    val exists: Boolean = flag in DiffFlag.Exists
}
