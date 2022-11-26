package com.kgit2.diff

import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import libgit2.git_diff_delta

@Raw(
    base = git_diff_delta::class,
)
class DiffDelta(raw: DiffDeltaRaw) : GitBase<git_diff_delta, DiffDeltaRaw>(raw) {
    constructor(memory: Memory, handler: DiffDeltaPointer) : this(DiffDeltaRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        secondary: DiffDeltaSecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: DiffDeltaSecondaryInitial? = null,
    ) : this(DiffDeltaRaw(memory, secondary, secondaryInitial))

    val path: String = raw.handler.pointed.new_file.path!!.toKString()

    val flag: DiffFlag = DiffFlag(raw.handler.pointed.flags)

    val nFiles: UShort = raw.handler.pointed.nfiles

    val status: Delta = Delta.fromRaw(raw.handler.pointed.status)

    val similarity: UShort = raw.handler.pointed.similarity

    val oldFile: DiffFile = DiffFile(Memory(), raw.handler.pointed.old_file.ptr)

    val newFile: DiffFile = DiffFile(Memory(), raw.handler.pointed.new_file.ptr)
}
