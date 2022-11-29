package com.kgit2.odb

import com.kgit2.checkout.IndexerProgressCallback
import com.kgit2.common.error.GitErrorCode
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.index.IndexerProgress
import com.kgit2.memory.BeforeFree
import com.kgit2.memory.GitBase
import com.kgit2.memory.Raw
import kotlinx.cinterop.*
import libgit2.git_indexer_progress
import libgit2.git_odb_writepack
import okio.Buffer
import okio.Sink
import okio.Timeout

typealias OdbPackWriterPointer = CPointer<git_odb_writepack>

typealias OdbPackWriterSecondaryPointer = CPointerVar<git_odb_writepack>

typealias OdbPackWriterInitial = OdbPackWriterSecondaryPointer.(Memory, OdbPackWriter.Progress) -> Unit

class OdbPackWriterRaw(
    memory: Memory,
    handler: OdbPackWriterPointer,
    val progress: CPointerVar<git_indexer_progress> = memory.allocPointerTo(),
) : Raw<git_odb_writepack>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: OdbPackWriterSecondaryPointer = memory.allocPointerTo(),
        progress: OdbPackWriter.Progress = OdbPackWriter.Progress(),
        initial: OdbPackWriterInitial? = null,
    ) : this(memory, handler.apply {
        runCatching {
            initial?.invoke(handler, memory, progress)
        }.onFailure {
            memory.free()
        }.getOrThrow()
    }.value!!)

    override var beforeFree: BeforeFree? = {
        handler.pointed.free?.invoke(handler)
    }
}

class OdbPackWriter(
    raw: OdbPackWriterRaw,
    protected val progress: Progress,
) : GitBase<git_odb_writepack, OdbPackWriterRaw>(raw), Sink {
    constructor(memory: Memory, handler: CPointer<git_odb_writepack>) : this(
        OdbPackWriterRaw(memory, handler),
        Progress()
    )

    constructor(
        memory: Memory = Memory(),
        handler: OdbPackWriterSecondaryPointer = memory.allocPointerTo(),
        progress: Progress = Progress(),
        initial: OdbPackWriterInitial? = null,
    ) : this(OdbPackWriterRaw(memory, handler, progress, initial), progress)

    fun setProgress(progressCallback: IndexerProgressCallback) {
        progress.progressCallback = progressCallback
    }

    fun commit() {
        raw.handler.pointed.commit?.invoke(
            raw.handler,
            raw.progress.value!!
        )?.errorCheck()
    }

    override fun close() {
        raw.free()
    }

    override fun flush() {}

    override fun timeout(): Timeout = Timeout.NONE

    override fun write(source: Buffer, byteCount: Long) {
        raw.handler.pointed.append?.invoke(
            raw.handler,
            source.readByteArray(byteCount).refTo(0).getPointer(raw.memory),
            byteCount.convert(),
            raw.progress.value
        )?.errorCheck()
    }

    class Progress(var progressCallback: IndexerProgressCallback? = null) : IndexerProgressCallback {
        override fun invoke(progress: IndexerProgress): GitErrorCode = progressCallback?.invoke(progress) ?: GitErrorCode.Ok
    }
}
