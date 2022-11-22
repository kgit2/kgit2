package com.kgit2.odb

import com.kgit2.common.error.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import com.kgit2.memory.Raw
import kotlinx.cinterop.*
import libgit2.git_odb_stream
import libgit2.git_odb_stream_free
import libgit2.git_odb_stream_read
import okio.Buffer
import okio.Source
import okio.Timeout

typealias OdbReaderPointer = CPointer<git_odb_stream>

typealias OdbReaderSecondaryPointer = CPointerVar<git_odb_stream>

typealias OdbReaderInitial = OdbReaderSecondaryPointer.(Memory) -> Unit

class OdbReaderRaw(
    memory: Memory = Memory(),
    handler: OdbReaderPointer = memory.alloc<git_odb_stream>().ptr,
) : Raw<git_odb_stream>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: OdbReaderSecondaryPointer = memory.allocPointerTo(),
        initial: OdbReaderInitial? = null,
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


class OdbReader(raw: OdbReaderRaw) : GitBase<git_odb_stream, OdbReaderRaw>(raw), Source {
    constructor(memory: Memory, handler: CPointer<git_odb_stream>) : this(OdbReaderRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: OdbReaderSecondaryPointer = memory.allocPointerTo(),
        initial: OdbReaderInitial? = null,
    ) : this(OdbReaderRaw(memory, handler, initial))

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
