package com.kgit2.diff

import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import com.kgit2.memory.Raw
import kotlinx.cinterop.*
import libgit2.git_diff_delta

typealias DiffDeltaPointer = CPointer<git_diff_delta>

typealias DiffDeltaSecondaryPointer = CPointerVar<git_diff_delta>

typealias DiffDeltaInitial = DiffDeltaSecondaryPointer.(Memory) -> Unit

class DiffDeltaRaw(
    memory: Memory,
    handler: DiffDeltaPointer,
) : Raw<git_diff_delta>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: DiffDeltaSecondaryPointer = memory.allocPointerTo(),
        initial: DiffDeltaInitial? = null,
    ) : this(memory, handler.apply {
        runCatching {
            initial?.invoke(handler, memory)
        }.onFailure {
            memory.free()
        }.getOrThrow()
    }.value!!)
}

class DiffDelta(raw: DiffDeltaRaw) : GitBase<git_diff_delta, DiffDeltaRaw>(raw) {
    constructor(memory: Memory, handler: DiffDeltaPointer) : this(DiffDeltaRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: DiffDeltaSecondaryPointer = memory.allocPointerTo(),
        initial: DiffDeltaInitial? = null
    ) : this(DiffDeltaRaw(memory, handler, initial))

    val path: String = raw.handler.pointed.new_file.path!!.toKString()

    val flag: DiffFlag = DiffFlag(raw.handler.pointed.flags)

    val nFiles: UShort = raw.handler.pointed.nfiles

    val status: Delta = Delta.fromRaw(raw.handler.pointed.status)

    val similarity: UShort = raw.handler.pointed.similarity

    val oldFile: DiffFile = DiffFile(Memory(), raw.handler.pointed.old_file.ptr)

    val newFile: DiffFile = DiffFile(Memory(), raw.handler.pointed.new_file.ptr)
}
