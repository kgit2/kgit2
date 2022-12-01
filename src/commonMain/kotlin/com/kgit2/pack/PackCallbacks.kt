package com.kgit2.pack

import com.kgit2.common.error.GitErrorCode
import com.kgit2.common.option.mutually.PackBuilderStage
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.convert
import kotlinx.cinterop.staticCFunction
import libgit2.git_packbuilder_progress
import platform.posix.uint32_t

/**
 * Callback for the user's custom packbuilder progress.
 *
 * @param stage The current stage of the packbuilder
 * @param current The current value of the stage
 * @param total The total value of the stage
 * @return 0 to proceed with the packbuilder, < 0 to fail the packbuilder
 */
typealias PackBuilderProgressCallback = (stage: PackBuilderStage, current: ULong, total: ULong) -> GitErrorCode

interface PackBuilderProgressCallbackPayload {
    var packBuilderProgressCallback: PackBuilderProgressCallback?
}

val staticPackBuilderProgressCallback: git_packbuilder_progress = staticCFunction {
        stage: Int,
        current: uint32_t,
        total: uint32_t,
        payload: COpaquePointer?,
    ->
    val callback = payload?.asStableRef<PackBuilderProgressCallbackPayload>()?.get()
    callback?.packBuilderProgressCallback?.invoke(
        PackBuilderStage.from(stage.convert()),
        current.toULong(),
        total.toULong()
    )?.value ?: 0
}

interface PackProgress {
    fun packProgress(
        stage: PackBuilderStage,
        current: Int,
        total: Int,
    )
}
