package com.kgit2.odb

import com.kgit2.common.error.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.convert
import kotlinx.cinterop.refTo
import libgit2.git_odb_stream
import libgit2.git_odb_stream_read
import okio.Buffer
import okio.Source
import okio.Timeout

class OdbReader(raw: OdbStreamRaw) : GitBase<git_odb_stream, OdbStreamRaw>(raw), Source {
    constructor(memory: Memory, handler: OdbStreamPointer) : this(OdbStreamRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        secondary: OdbStreamSecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: OdbStreamSecondaryInitial? = null,
    ) : this(OdbStreamRaw(memory, secondary, secondaryInitial))

    /**
     * Closes this source and releases the resources held by this source. It is an error to read a
     * closed source. It is safe to close a source more than once.
     */
    override fun close() {
        raw.free()
    }

    /**
     * Removes at least 1, and up to `byteCount` bytes from this and appends them to `sink`. Returns
     * the number of bytes read, or -1 if this source is exhausted.
     */
    override fun read(sink: Buffer, byteCount: Long): Long {
        val buf = ByteArray(byteCount.toInt()) { 0 }
        git_odb_stream_read(raw.handler, buf.refTo(0), byteCount.convert()).errorCheck()
        sink.write(buf)
        return buf.count { it != 0.toByte() }.toLong()
    }

    /** Returns the timeout for this source.  */
    override fun timeout(): Timeout = Timeout.NONE
}
