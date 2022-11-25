package com.kgit2.odb

import com.kgit2.common.error.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import com.kgit2.memory.Raw
import kotlinx.cinterop.*
import libgit2.git_odb_stream
import libgit2.git_odb_stream_free
import libgit2.git_odb_stream_write
import okio.Buffer
import okio.Sink
import okio.Timeout

typealias OdbWriterPointer = CPointer<git_odb_stream>

typealias OdbWriterSecondaryPointer = CPointerVar<git_odb_stream>

typealias OdbWriterInitial = OdbWriterSecondaryPointer.(Memory) -> Unit

class OdbWriterRaw(
    memory: Memory = Memory(),
    handler: OdbWriterPointer = memory.alloc<git_odb_stream>().ptr,
) : Raw<git_odb_stream>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: OdbWriterSecondaryPointer = memory.allocPointerTo(),
        initial: OdbWriterInitial? = null,
    ) : this(memory, handler.apply {
        runCatching {
            initial?.invoke(handler, memory)
        }.onFailure {
            memory.free()
        }.getOrThrow()
    }.value!!)

    override val beforeFree: () -> Unit = {
        git_odb_stream_free(handler)
    }
}


class OdbWriter(raw: OdbWriterRaw) : GitBase<git_odb_stream, OdbWriterRaw>(raw), Sink {
    constructor(memory: Memory, handler: CPointer<git_odb_stream>) : this(OdbWriterRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: OdbWriterSecondaryPointer = memory.allocPointerTo(),
        initial: OdbWriterInitial? = null,
    ) : this(OdbWriterRaw(memory, handler, initial))

    override fun close() {
        raw.free()
    }

    override fun flush() {
    }

    override fun timeout(): Timeout = Timeout.NONE

    override fun write(source: Buffer, byteCount: Long) {
        val buffer = source.readUtf8LineStrict(byteCount)
        git_odb_stream_write(raw.handler, buffer, byteCount.convert()).errorCheck()
    }
}
