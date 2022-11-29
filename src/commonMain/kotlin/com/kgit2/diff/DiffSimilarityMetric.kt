package com.kgit2.diff

import com.kgit2.annotations.Raw
import com.kgit2.common.error.GitErrorCode
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import com.kgit2.signature.Signature
import kotlinx.cinterop.*
import libgit2.git_diff_similarity_metric
import kotlin.native.internal.Cleaner
import kotlin.native.internal.createCleaner

typealias FileSignature = (CPointer<COpaquePointerVar>?, DiffFile?, String?) -> GitErrorCode
typealias BufferSignature = (CPointer<COpaquePointerVar>?, DiffFile?, String?) -> GitErrorCode
typealias FreeSignature = (COpaquePointer?) -> Unit
typealias Similarity = (Int?, Signature?, Signature?) -> GitErrorCode

@Raw(
    base = git_diff_similarity_metric::class,
)
class DiffSimilarityMetric(
    raw: DiffSimilarityMetricRaw = DiffSimilarityMetricRaw(),
) : GitBase<git_diff_similarity_metric, DiffSimilarityMetricRaw>(raw) {
    inner class CallbackPayload {
        var fileSignature: FileSignature? = null
        var bufferSignature: BufferSignature? = null
        var freeSignature: FreeSignature? = null
        var similarity: Similarity? = null
    }

    private val callbackPayload = CallbackPayload()
    private val stableRef = StableRef.create(callbackPayload)

    init {
        raw.handler.pointed.payload = stableRef.asCPointer()
    }

    override val cleaner: Cleaner = createCleaner(raw to stableRef) {
        it.second.dispose()
        it.first.free()
    }

    var fileSignature: FileSignature?
        get() = callbackPayload.fileSignature
        set(value) {
            callbackPayload.fileSignature = value
            raw.handler.pointed.file_signature = value?.let {
                staticCFunction { out, file, fullPath, payload ->
                    val callback = payload!!.asStableRef<CallbackPayload>().get()
                    callback.fileSignature!!.invoke(
                        out,
                        file?.let { DiffFile(Memory(), it) },
                        fullPath?.toKString()
                    ).value
                }
            }
        }

    var bufferSignature: BufferSignature?
        get() = callbackPayload.bufferSignature
        set(value) {
            callbackPayload.bufferSignature = value
            raw.handler.pointed.buffer_signature = value?.let {
                staticCFunction { out, file, buffer, size, payload ->
                    val callback = payload!!.asStableRef<CallbackPayload>().get()
                    callback.bufferSignature!!.invoke(
                        out,
                        file?.let { DiffFile(Memory(), file) },
                        buffer?.readBytes(size.convert())?.toKString()
                    ).value
                }
            }
        }

    var freeSignature: FreeSignature?
        get() = callbackPayload.freeSignature
        set(value) {
            callbackPayload.freeSignature = value
            raw.handler.pointed.free_signature = value?.let {
                staticCFunction { sig, payload ->
                    val callback = payload!!.asStableRef<CallbackPayload>().get()
                    callback.freeSignature!!.invoke(sig)
                }
            }
        }

    var similarity: Similarity?
        get() = callbackPayload.similarity
        set(value) {
            callbackPayload.similarity = value
            raw.handler.pointed.similarity = value?.let {
                staticCFunction { score, a, b, payload ->
                    val callback = payload!!.asStableRef<CallbackPayload>().get()
                    callback.similarity!!.invoke(
                        score?.pointed?.value,
                        a?.let { Signature(Memory(), it.reinterpret()) },
                        b?.let { Signature(Memory(), it.reinterpret()) }
                    ).value
                }
            }
        }
}
