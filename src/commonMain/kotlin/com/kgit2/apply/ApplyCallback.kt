package com.kgit2.apply

import com.kgit2.common.callback.CallbackResult
import com.kgit2.common.error.GitErrorCode
import com.kgit2.diff.DiffDelta
import com.kgit2.diff.DiffHunk
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.staticCFunction
import libgit2.git_apply_delta_cb
import libgit2.git_apply_hunk_cb
import libgit2.git_diff_hunk

/**
 * @return [CallbackResult]
 * the delta will not be applied, but the apply process continues - [CallbackResult.Ok]
 * the apply process will be aborted - [CallbackResult.Abort]
 * the delta will not be applied, but the apply process continues - [CallbackResult.Skip]
 */
typealias ApplyDeltaCallback = (delta: DiffDelta) -> CallbackResult

interface ApplyDeltaCallbackPayload {
    var applyDeltaCallback: ApplyDeltaCallback?
}

val staticApplyDeltaCallback: git_apply_delta_cb = staticCFunction {
        delta, payload: COpaquePointer?,
    ->
    val callbackPayload = payload?.asStableRef<ApplyDeltaCallbackPayload>()?.get()
    callbackPayload?.applyDeltaCallback?.invoke(DiffDelta(handler = delta!!))?.value ?: CallbackResult.Ok.value
}

/**
 * @return [CallbackResult]
 * if the hunk is applied - [CallbackResult.Ok]
 * if the hunk is not applied - [CallbackResult.Skip]
 * if the apply process should be aborted - [CallbackResult.Abort]
 */
typealias ApplyHunkCallback = (hunk: DiffHunk) -> CallbackResult

interface ApplyHunkCallbackPayload {
    var applyHunkCallback: ApplyHunkCallback?
}

val staticApplyHunkCallback: git_apply_hunk_cb = staticCFunction {
        hunk, payload: COpaquePointer?,
    ->
    val callbackPayload = payload?.asStableRef<ApplyHunkCallbackPayload>()?.get()
    callbackPayload?.applyHunkCallback?.invoke(DiffHunk(handler = hunk!!))?.value ?: CallbackResult.Ok.value
}
