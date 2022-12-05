package com.kgit2.diff

import com.kgit2.annotations.Raw
import com.kgit2.common.error.GitErrorCode
import com.kgit2.common.extend.asStableRef
import com.kgit2.common.memory.Memory
import com.kgit2.memory.CallbackAble
import com.kgit2.memory.ICallbacksPayload
import com.kgit2.memory.RawWrapper
import com.kgit2.memory.createCleaner
import com.kgit2.signature.Signature
import kotlinx.cinterop.*
import libgit2.git_diff_binary
import libgit2.git_diff_file
import libgit2.git_diff_similarity_metric
import platform.posix.size_t
import kotlin.native.internal.Cleaner

@Raw(
    base = git_diff_similarity_metric::class,
)
class DiffSimilarityMetric(
    raw: DiffSimilarityMetricRaw = DiffSimilarityMetricRaw(),
    initial: DiffSimilarityMetric.() -> Unit = {},
) : RawWrapper<git_diff_similarity_metric, DiffSimilarityMetricRaw>(raw),
    CallbackAble<git_diff_similarity_metric, DiffSimilarityMetricRaw, DiffSimilarityMetric.CallbacksPayload> {
    override val callbacksPayload: CallbacksPayload = CallbacksPayload()

    override val stableRef: StableRef<CallbacksPayload> = callbacksPayload.asStableRef()

    override val cleaner: Cleaner = createCleaner()

    init {
        raw.handler.pointed.payload = stableRef.asCPointer()
    }

    var fileSignature: FileSignature? by callbacksPayload::fileSignature

    var bufferSignature: BufferSignature? by callbacksPayload::bufferSignature

    var freeSignature: FreeSignature? by callbacksPayload::freeSignature

    var similarity: Similarity? by callbacksPayload::similarity

    init {
        this.initial()
    }

    inner class CallbacksPayload
        : ICallbacksPayload,
        FileSignaturePayload,
        BufferSignaturePayload,
        FreeSignaturePayload,
        SimilarityPayload {
        override var fileSignature: FileSignature? = null
            set(value) {
                field = value
                raw.handler.pointed.file_signature = value?.let { staticFileSignature }
            }
        override var bufferSignature: BufferSignature? = null
            set(value) {
                field = value
                raw.handler.pointed.buffer_signature = value?.let { staticBufferSignature }
            }
        override var freeSignature: FreeSignature? = null
            set(value) {
                field = value
                raw.handler.pointed.free_signature = value?.let { staticFreeSignature }
            }
        override var similarity: Similarity? = null
            set(value) {
                field = value
                raw.handler.pointed.similarity = value?.let { staticSimilarity }
            }
    }
}

typealias FileSignature = (CPointer<COpaquePointerVar>?, DiffFile?, String?) -> GitErrorCode
typealias git_diff_file_signature_cb = CPointer<CFunction<(CPointer<COpaquePointerVar>?, CPointer<git_diff_file>?, CPointer<ByteVar>?, COpaquePointer?) -> Int>>

interface FileSignaturePayload {
    var fileSignature: FileSignature?
}

val staticFileSignature: git_diff_file_signature_cb = staticCFunction {
        out: CPointer<COpaquePointerVar>?,
        file: CPointer<git_diff_file>?,
        fullPath: CPointer<ByteVar>?,
        payload: COpaquePointer?,
    ->
    val callback = payload!!.asStableRef<FileSignaturePayload>().get()
    callback.fileSignature!!.invoke(out, file?.let { DiffFile(Memory(), it) }, fullPath?.toKString()).value
}

typealias BufferSignature = (CPointer<COpaquePointerVar>?, DiffFile?, String?) -> GitErrorCode
typealias git_diff_buffer_signature_cb = CPointer<CFunction<(CPointer<COpaquePointerVar>?, CPointer<git_diff_file>?, CPointer<ByteVar>?, size_t, COpaquePointer?) -> Int>>

interface BufferSignaturePayload {
    var bufferSignature: BufferSignature?
}

val staticBufferSignature: git_diff_buffer_signature_cb = staticCFunction {
        out: CPointer<COpaquePointerVar>?,
        file: CPointer<git_diff_file>?,
        buffer: CPointer<ByteVar>?,
        size: size_t,
        payload: COpaquePointer?,
    ->
    val callback = payload!!.asStableRef<BufferSignaturePayload>().get()
    callback.bufferSignature!!.invoke(
        out,
        file?.let { DiffFile(Memory(), file) },
        buffer?.readBytes(size.convert())?.toKString()
    ).value
}

typealias FreeSignature = (COpaquePointer?) -> Unit
typealias git_diff_free_signature_cb = CPointer<CFunction<(COpaquePointer?, COpaquePointer?) -> Unit>>

interface FreeSignaturePayload {
    var freeSignature: FreeSignature?
}

val staticFreeSignature: git_diff_free_signature_cb = staticCFunction {
        sig: COpaquePointer?,
        payload: COpaquePointer?,
    ->
    val callback = payload!!.asStableRef<FreeSignaturePayload>().get()
    callback.freeSignature!!.invoke(sig)
}

typealias Similarity = (Int?, Signature?, Signature?) -> GitErrorCode
typealias git_diff_similarity_cb = CPointer<CFunction<(CPointer<IntVar>?, COpaquePointer?, COpaquePointer?, COpaquePointer?) -> Int>>

interface SimilarityPayload {
    var similarity: Similarity?
}

val staticSimilarity: git_diff_similarity_cb = staticCFunction {
        score: CPointer<IntVar>?,
        a: COpaquePointer?,
        b: COpaquePointer?,
        payload: COpaquePointer?,
    ->
    val callback = payload!!.asStableRef<SimilarityPayload>().get()
    callback.similarity!!.invoke(
        score?.pointed?.value,
        a?.let { Signature(Memory(), it.reinterpret()) },
        b?.let { Signature(Memory(), it.reinterpret()) }
    ).value
}
