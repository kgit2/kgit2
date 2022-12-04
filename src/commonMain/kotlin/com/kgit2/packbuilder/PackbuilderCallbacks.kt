package com.kgit2.packbuilder

import com.kgit2.common.error.GitErrorCode
import kotlinx.cinterop.*
import libgit2.git_packbuilder_foreach_cb
import libgit2.git_packbuilder_progress
import platform.posix.size_t
import platform.posix.uint32_t

/**
 * Callback for the user's custom packbuilder progress.
 *
 * @param stage The current stage of the packbuilder
 * @param current The current value of the stage
 * @param total The total value of the stage
 * @return 0 to proceed with the packbuilder, < 0 to fail the packbuilder
 */
typealias PackbuilderProgressCallback = (stage: PackBuilderStage, current: ULong, total: ULong) -> GitErrorCode

interface PackbuilderProgressCallbackPayload {
    var packbuilderProgressCallback: PackbuilderProgressCallback?
}

val staticPackbuilderProgressCallback: git_packbuilder_progress = staticCFunction {
        stage: Int,
        current: uint32_t,
        total: uint32_t,
        payload: COpaquePointer?,
    ->
    val callback = payload?.asStableRef<PackbuilderProgressCallbackPayload>()?.get()
    callback?.packbuilderProgressCallback?.invoke(
        PackBuilderStage.from(stage.convert()),
        current.toULong(),
        total.toULong()
    )?.value ?: 0
}

typealias PackbuilderForeachCallback = (buffer: ByteArray) -> GitErrorCode

interface PackbuilderForeachCallbackPayload {
    var packbuilderForeachCallback: PackbuilderForeachCallback?
}

val staticPackbuilderForeachCallback: git_packbuilder_foreach_cb = staticCFunction {
        buffer: COpaquePointer?, size: size_t, payload: COpaquePointer?,
    ->
    val callbackPayload = payload?.asStableRef<PackbuilderForeachCallbackPayload>()?.get()
    callbackPayload?.packbuilderForeachCallback?.invoke(buffer!!.readBytes(size.convert()))?.value
        ?: GitErrorCode.Ok.value
}

interface PackProgress {
    fun packProgress(
        stage: PackBuilderStage,
        current: Int,
        total: Int,
    )
}
