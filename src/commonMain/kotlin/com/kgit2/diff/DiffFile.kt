package com.kgit2.diff

import com.kgit2.common.memory.Memory
import com.kgit2.common.option.mutually.FileMode
import com.kgit2.memory.GitBase
import com.kgit2.memory.Raw
import com.kgit2.model.Oid
import kotlinx.cinterop.*
import libgit2.git_diff_file

typealias DiffFilePointer = CPointer<git_diff_file>

typealias DiffFileSecondaryPointer = CPointerVar<git_diff_file>

typealias DiffFileInitial = DiffFilePointer.(Memory) -> Unit

class DiffFileRaw(
    memory: Memory,
    handler: DiffFilePointer,
    initial: DiffFileInitial? = null,
) : Raw<git_diff_file>(memory, handler.apply {
    runCatching {
        initial?.invoke(handler, memory)
    }.onFailure {
        memory.free()
    }.getOrThrow()
})

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
