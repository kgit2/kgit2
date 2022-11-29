package com.kgit2.diff

import cnames.structs.git_diff
import com.kgit2.annotations.Raw
import com.kgit2.common.extend.asCPointer
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.extend.toBoolean
import com.kgit2.common.memory.Memory
import com.kgit2.memory.IterableBase
import com.kgit2.oid.Oid
import kotlinx.cinterop.*
import libgit2.*

@Raw(
    base = git_diff::class,
    free = "git_diff_free",
)
class Diff(raw: DiffRaw): IterableBase<git_diff, DiffRaw, DiffDelta>(raw) {
    constructor(
        memory: Memory = Memory(),
        secondary: DiffSecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: DiffSecondaryInitial? = null,
    ) : this(DiffRaw(memory, secondary, secondaryInitial))

    constructor(content: ByteArray) : this(secondaryInitial = {
        git_diff_from_buffer(
            this.ptr,
            content.contentToString(),
            content.size.convert()
        ).errorCheck()
    })

    constructor(content: String) : this(secondaryInitial = {
        git_diff_from_buffer(
            this.ptr,
            content,
            content.length.convert()
        ).errorCheck()
    })

    override val size: Long = git_diff_num_deltas(raw.handler).toLong()

    override operator fun get(index: Long): DiffDelta = git_diff_get_delta(raw.handler, index.convert())?.let {
        DiffDelta(handler = it)
    } ?: throw IndexOutOfBoundsException()

    fun merge(from: Diff) {
        git_diff_merge(raw.handler, from.raw.handler).errorCheck()
    }

    fun isSortedICase(): Boolean = git_diff_is_sorted_icase(raw.handler).toBoolean()

    fun print(format: DiffFormat, printCallback: DiffLineCallback) {
        git_diff_print(
            raw.handler,
            format.value,
            staticCFunction { delta, hunk, line, payload ->
                val callbackPayload = payload!!.asStableRef<DiffLineCallback>()
                val result = callbackPayload.get().invoke(
                    delta?.let { DiffDelta(handler = it) },
                    hunk?.let { DiffHunk(handler = it) },
                    line?.let { DiffLine(handler = it) },
                ).value
                callbackPayload.dispose()
                result
            },
            printCallback.asCPointer()
        ).errorCheck()
    }

    fun forEach(fileCallback: DiffFileCallback, binaryCallback: DiffBinaryCallback?, hunkCallback: DiffHunkCallback?, lineCallback: DiffLineCallback?) {
        data class CallbackPayload(
            val fileCallback: DiffFileCallback,
            val binaryCallback: DiffBinaryCallback?,
            val hunkCallback: DiffHunkCallback?,
            val lineCallback: DiffLineCallback?,
        )
        val callbackPayload = CallbackPayload(fileCallback, binaryCallback, hunkCallback, lineCallback)
        git_diff_foreach(
            raw.handler,
            staticCFunction { delta, progress, payload ->
                val callbackPayload = payload!!.asStableRef<CallbackPayload>()
                val result = callbackPayload.get().fileCallback.invoke(
                    delta?.let { DiffDelta(handler = it) },
                    progress
                ).value
                callbackPayload.dispose()
                result
            },
            binaryCallback?.let {
                staticCFunction { delta, oldFile, payload ->
                    val callbackPayload = payload!!.asStableRef<CallbackPayload>()
                    val result = callbackPayload.get().binaryCallback!!.invoke(
                        delta?.let { DiffDelta(handler = it) },
                        oldFile?.let { DiffBinary(handler = it) },
                    ).value
                    callbackPayload.dispose()
                    result
                }
            },
            hunkCallback?.let {
                staticCFunction { delta, hunk, payload ->
                    val callbackPayload = payload!!.asStableRef<CallbackPayload>()
                    val result = callbackPayload.get().hunkCallback!!.invoke(
                        delta?.let { DiffDelta(handler = it) },
                        hunk?.let { DiffHunk(handler = it) },
                    ).value
                    callbackPayload.dispose()
                    result
                }
            },
            lineCallback?.let {
                staticCFunction { delta, hunk, line, payload ->
                    val callbackPayload = payload!!.asStableRef<CallbackPayload>()
                    val result = callbackPayload.get().lineCallback!!.invoke(
                        delta?.let { DiffDelta(handler = it) },
                        hunk?.let { DiffHunk(handler = it) },
                        line?.let { DiffLine(handler = it) },
                    ).value
                    callbackPayload.dispose()
                    result
                }
            },
            callbackPayload.asCPointer()
        ).errorCheck()
    }

    fun stats(): DiffStats = DiffStats {
        git_diff_get_stats(this.ptr, raw.handler).errorCheck()
    }

    fun findSimilar(options: DiffFindOptions?) {
        git_diff_find_similar(raw.handler, options?.raw?.handler).errorCheck()
    }

    fun patchID(options: DiffPatchIDOptions? = null): Oid = Oid {
        git_diff_patchid(this, raw.handler, options?.raw?.handler).errorCheck()
    }

    fun numDeltasOfType(type: DiffDeltaType): Long = git_diff_num_deltas_of_type(raw.handler, type.value).toLong()
}
