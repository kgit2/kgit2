package com.kgit2.blob

import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.memory.RawWrapper
import kotlinx.cinterop.convert
import kotlinx.cinterop.invoke
import kotlinx.cinterop.pointed
import kotlinx.cinterop.refTo
import libgit2.git_writestream
import okio.Buffer
import okio.Sink
import okio.Timeout

@Raw(
    base = git_writestream::class,
    beforeFree = "handler.pointed.free?.invoke(handler)"
)
class BlobWriter(
    raw: WritestreamRaw
) : RawWrapper<git_writestream, WritestreamRaw>(raw), Sink {
    constructor(secondaryInitial: WritestreamSecondaryInitial) : this(WritestreamRaw(secondaryInitial = secondaryInitial))

    override fun close() {
        raw.free()
    }

    override fun flush() {}

    override fun timeout(): Timeout = Timeout.NONE

    override fun write(source: Buffer, byteCount: Long) {
        raw.handler.pointed.write?.invoke(
            raw.handler,
            source.readByteArray(byteCount).refTo(0).getPointer(raw.memory),
            byteCount.convert()
        )
    }
}
