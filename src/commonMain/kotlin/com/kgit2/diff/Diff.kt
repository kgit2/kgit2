package com.kgit2.diff

import cnames.structs.git_diff
import com.kgit2.annotations.Raw
import com.kgit2.common.extend.asStableRef
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.extend.toBoolean
import com.kgit2.common.memory.Memory
import com.kgit2.memory.IterableBase
import com.kgit2.memory.RawWrapper
import com.kgit2.oid.Oid
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.convert
import kotlinx.cinterop.ptr
import libgit2.*

@Raw(
    base = git_diff::class,
    free = "git_diff_free",
)
class Diff(raw: DiffRaw) : RawWrapper<git_diff, DiffRaw>(raw), IterableBase<DiffDelta> {
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
        val callbackPayload = CallbackPayload(diffLineCallback = printCallback).asStableRef()
        git_diff_print(
            raw.handler,
            format.value,
            staticDiffLineCallback,
            callbackPayload.asCPointer()
        ).errorCheck()
        callbackPayload.dispose()
    }

    fun forEach(
        fileCallback: DiffFileCallback,
        binaryCallback: DiffBinaryCallback?,
        hunkCallback: DiffHunkCallback?,
        lineCallback: DiffLineCallback?,
    ) {
        val callbackPayload = CallbackPayload(fileCallback, binaryCallback, hunkCallback, lineCallback).asStableRef()
        git_diff_foreach(
            raw.handler,
            staticDiffFileCallback,
            binaryCallback?.let { staticDiffBinaryCallback },
            hunkCallback?.let { staticDiffHunkCallback },
            lineCallback?.let { staticDiffLineCallback },
            callbackPayload.asCPointer()
        ).errorCheck()
        callbackPayload.dispose()
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

    data class CallbackPayload(
        override var diffFileCallback: DiffFileCallback? = null,
        override var diffBinaryCallback: DiffBinaryCallback? = null,
        override var diffHunkCallback: DiffHunkCallback? = null,
        override var diffLineCallback: DiffLineCallback? = null,
    ) : DiffFileCallbackPayload,
        DiffBinaryCallbackPayload,
        DiffHunkCallbackPayload,
        DiffLineCallbackPayload
}
