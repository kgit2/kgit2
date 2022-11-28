package com.kgit2.diff

import com.kgit2.common.extend.asStableRef
import com.kgit2.common.memory.Memory
import com.kgit2.memory.BeforeFree
import com.kgit2.memory.GitBase
import com.kgit2.memory.Raw
import com.kgit2.signature.Signature
import kotlinx.cinterop.*
import libgit2.git_diff_similarity_metric

typealias DiffSimilarityMetricValue = CValue<git_diff_similarity_metric>

typealias DiffSimilarityMetricPointer = CPointer<git_diff_similarity_metric>

typealias DiffSimilarityMetricInitial = DiffSimilarityMetricPointer.(Memory) -> Unit

typealias DiffSimilarityMetricSecondaryPointer = CPointerVar<git_diff_similarity_metric>

typealias DiffSimilarityMetricSecondaryInitial = DiffSimilarityMetricSecondaryPointer.(Memory) -> Unit

class DiffSimilarityMetricRaw(
    memory: Memory = Memory(),
    handler: DiffSimilarityMetricPointer = memory.alloc<git_diff_similarity_metric>().ptr,
) : Raw<git_diff_similarity_metric>(memory, handler) {
    private val stableRef: StableRef<DiffSimilarityMetricRaw> = this.asStableRef()

    init {
        runCatching {
            handler.pointed.payload = this.stableRef.asCPointer()
        }.onFailure {
            memory.free()
        }.getOrThrow()
    }

    override val beforeFree: BeforeFree = {
        stableRef.dispose()
    }

    var fileSignature: ((CPointer<COpaquePointerVar>?, DiffFile?, String?) -> Int)? = null
        set(value) {
            field = value
            handler.pointed.file_signature = value?.let {
                staticCFunction { out, file, fullPath, payload ->
                    val callback = payload!!.asStableRef<DiffSimilarityMetricRaw>().get()
                    callback.fileSignature!!.invoke(out, file?.let { DiffFile(Memory(), it) }, fullPath?.toKString())
                }
            }
        }

    var bufferSignature: ((CPointer<COpaquePointerVar>?, DiffFile?, String?) -> Int)? = null
        set(value) {
            field = value
            handler.pointed.buffer_signature = value?.let {
                staticCFunction { out, file, buffer, size, payload ->
                    val callback = payload!!.asStableRef<DiffSimilarityMetricRaw>().get()
                    callback.bufferSignature!!.invoke(out, file?.let { DiffFile(Memory(), file) }, buffer?.readBytes(size.convert())?.toKString())
                }
            }
        }

    var freeSignature: ((COpaquePointer?) -> Unit)? = null
        set(value) {
            field = value
            handler.pointed.free_signature = value?.let {
                staticCFunction { sig, payload ->
                    val callback = payload!!.asStableRef<DiffSimilarityMetricRaw>().get()
                    callback.freeSignature!!.invoke(sig)
                }
            }
        }

    var similarity: ((Int?, Signature?, Signature?) -> Int)? = null
        set(value) {
            field = value
            handler.pointed.similarity = value?.let {
                staticCFunction { score, a, b, payload ->
                    val callback = payload!!.asStableRef<DiffSimilarityMetricRaw>().get()
                    callback.similarity!!.invoke(
                        score?.pointed?.value,
                        a?.let { Signature(Memory(), it.reinterpret()) },
                        b?.let { Signature(Memory(), it.reinterpret()) }
                    )
                }
            }
        }
}

class DiffSimilarityMetric(
    raw: DiffSimilarityMetricRaw = DiffSimilarityMetricRaw(),
) : GitBase<git_diff_similarity_metric, DiffSimilarityMetricRaw>(raw) {
    var fileSignature: ((CPointer<COpaquePointerVar>?, DiffFile?, String?) -> Int)? by raw::fileSignature

    var bufferSignature: ((CPointer<COpaquePointerVar>?, DiffFile?, String?) -> Int)? by raw::bufferSignature

    var freeSignature: ((COpaquePointer?) -> Unit)? by raw::freeSignature

    var similarity: ((Int?, Signature?, Signature?) -> Int)? by raw::similarity
}
