package com.kgit2.odb

import com.kgit2.annotations.Raw
import com.kgit2.checkout.IndexerProgressCallback
import com.kgit2.checkout.IndexerProgressCallbackPayload
import com.kgit2.checkout.staticIndexerProgressCallback
import com.kgit2.common.extend.asStableRef
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.CallbackAble
import com.kgit2.memory.ICallbacksPayload
import com.kgit2.memory.RawWrapper
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.convert
import kotlinx.cinterop.invoke
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.usePinned
import libgit2.git_indexer_progress
import libgit2.git_odb_write_pack
import libgit2.git_odb_writepack
import okio.Buffer
import okio.Sink
import okio.Timeout
import kotlin.native.internal.Cleaner
import kotlin.native.internal.createCleaner

typealias OdbPackWriterProgress = IndexerProgressCallback

typealias OdbPackWriterProgressInitial = (Memory, StableRef<OdbPackWriter.CallbacksPayload>) -> OdbWritepackSecondaryInitial

@Raw(
    base = git_odb_writepack::class,
    beforeFree = "handler.pointed.free?.invoke(handler)"
)
class OdbPackWriter(
    raw: OdbWritepackRaw,
    override val callbacksPayload: CallbacksPayload = CallbacksPayload(),
    override val stableRef: StableRef<CallbacksPayload> = callbacksPayload.asStableRef(),
) : RawWrapper<git_odb_writepack, OdbWritepackRaw>(raw),
    CallbackAble<git_odb_writepack, OdbWritepackRaw, OdbPackWriter.CallbacksPayload>, Sink {
    constructor(memory: Memory, handler: CPointer<git_odb_writepack>) : this(OdbWritepackRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        secondary: OdbWritepackSecondaryPointer = memory.allocPointerTo(),
        callbacksPayload: CallbacksPayload = CallbacksPayload(),
        stableRef: StableRef<CallbacksPayload> = callbacksPayload.asStableRef(),
        initial: OdbPackWriterProgressInitial,
    ) : this(OdbWritepackRaw(memory, secondary, initial(memory, stableRef)), callbacksPayload, stableRef)

    override val cleaner: Cleaner = createCleaner(raw to stableRef) {
        it.second.dispose()
        it.first.free()
    }

    var progress: OdbPackWriterProgress? by callbacksPayload::indexerProgressCallback

    fun commit() {
        raw.handler.pointed.commit?.invoke(
            raw.handler,
            raw.memory.alloc<git_indexer_progress>().ptr
        )?.errorCheck()
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
            raw.handler.pointed.append!!.invoke(
                raw.handler,
                it.addressOf(0),
                buffer.size.convert(),
                raw.memory.alloc<git_indexer_progress>().ptr
            )
        }
    }

    class CallbacksPayload : ICallbacksPayload, IndexerProgressCallbackPayload {
        override var indexerProgressCallback: IndexerProgressCallback? = null
    }

    companion object {
        fun odbWritePack(odb: Odb): OdbPackWriter = OdbPackWriter(initial = { _, stableRef ->
            {
                git_odb_write_pack(
                    this.ptr,
                    odb.raw.handler,
                    staticIndexerProgressCallback,
                    stableRef.asCPointer()
                ).errorCheck()
            }
        })
    }
}
