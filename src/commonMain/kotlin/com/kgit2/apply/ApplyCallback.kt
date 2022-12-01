package com.kgit2.apply

import com.kgit2.common.error.GitErrorCode
import com.kgit2.diff.DiffDelta
import com.kgit2.diff.DiffHunk
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.staticCFunction
import libgit2.git_apply_delta_cb
import libgit2.git_apply_hunk_cb
import libgit2.git_diff_delta
import libgit2.git_diff_hunk

typealias ApplyDeltaCallback = (delta: DiffDelta) -> GitErrorCode

interface ApplyDeltaCallbackPayload {
    var applyDeltaCallback: ApplyDeltaCallback?
}

val staticApplyDeltaCallback: git_apply_delta_cb = staticCFunction {
        delta: CPointer<git_diff_delta>?,
        payload: COpaquePointer?,
    ->
    payload!!.asStableRef<ApplyDeltaCallbackPayload>().get()
        .applyDeltaCallback!!.invoke(DiffDelta(handler = delta!!)).value
}

typealias ApplyHunkCallback = (hunk: DiffHunk) -> GitErrorCode

interface ApplyHunkCallbackPayload {
    var applyHunkCallback: ApplyHunkCallback?
}

val staticApplyHunkCallback: git_apply_hunk_cb = staticCFunction {
        hunk: CPointer<git_diff_hunk>?,
        payload: COpaquePointer?,
    ->
    payload!!.asStableRef<ApplyHunkCallbackPayload>().get()
        .applyHunkCallback!!.invoke(DiffHunk(handler = hunk!!)).value
}
