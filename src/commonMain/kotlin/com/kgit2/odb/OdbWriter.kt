package com.kgit2.odb

import com.kgit2.common.extend.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.convert
import libgit2.git_odb_stream
import libgit2.git_odb_stream_write
import okio.Buffer
import okio.Sink
import okio.Timeout

class OdbWriter(raw: OdbStreamRaw) : GitBase<git_odb_stream, OdbStreamRaw>(raw), Sink {
    constructor(memory: Memory, handler: OdbStreamPointer) : this(OdbStreamRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        secondary: OdbStreamSecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: OdbStreamSecondaryInitial? = null,
    ) : this(OdbStreamRaw(memory, secondary, secondaryInitial))

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
