package com.kgit2.diff

import com.kgit2.annotations.Raw
import com.kgit2.common.error.GitErrorCode
import com.kgit2.common.memory.Memory
import com.kgit2.common.option.mutually.FileMode
import com.kgit2.memory.GitBase
import com.kgit2.oid.Oid
import kotlinx.cinterop.*
import libgit2.git_diff_file
import libgit2.git_diff_file_cb

typealias DiffFileCallback = (delta: DiffDelta?, progress: Float?) -> GitErrorCode

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

    val flags: DiffFlag = DiffFlag(raw.handler.pointed.flags)
}
