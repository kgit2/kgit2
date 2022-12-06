package com.kgit2.blob

import com.kgit2.annotations.Raw
import com.kgit2.common.extend.errorCheck
import com.kgit2.memory.RawWrapper
import com.kgit2.oid.Oid
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.invoke
import kotlinx.cinterop.pointed
import kotlinx.cinterop.usePinned
import libgit2.git_blob_create_from_stream_commit
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

    fun commit(): Oid {
        val result = Oid {
            git_blob_create_from_stream_commit(this, raw.handler).errorCheck()
        }
        raw.memory.free()
        raw.move()
        return result
    }

    override fun close() {
        raw.free()
    }

    override fun flush() {}

    override fun timeout(): Timeout = Timeout.NONE

    override fun write(source: Buffer, byteCount: Long) {
        write(source.readByteArray(byteCount.convert()))
    }

    fun write(buffer: ByteArray): Int {
        return buffer.usePinned {
            raw.handler.pointed.write!!.invoke(
                raw.handler,
                it.addressOf(0),
                buffer.size.convert()
            )
        }
    }
}
