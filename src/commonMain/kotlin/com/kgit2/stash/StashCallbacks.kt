package com.kgit2.stash

import com.kgit2.common.callback.CallbackResult
import com.kgit2.oid.Oid
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.toKString
import libgit2.git_oid
import libgit2.git_stash_apply_progress_cb
import libgit2.git_stash_apply_progress_t
import libgit2.git_stash_cb
import platform.posix.size_t

typealias StashApplyProgressCallback = (type: StashApplyProgressType) -> CallbackResult

interface StashApplyProgressCallbackPayload {
    var stashApplyProgressCallback: StashApplyProgressCallback?
}

val staticStashApplyProgressCallback: git_stash_apply_progress_cb = staticCFunction {
        type: git_stash_apply_progress_t, payload: COpaquePointer?,
    ->
    val callbackPayload = payload?.asStableRef<StashApplyProgressCallbackPayload>()?.get()
    callbackPayload?.stashApplyProgressCallback?.invoke(StashApplyProgressType.from(type))?.value
        ?: CallbackResult.Ok.value
}

typealias StashCallback = (index: ULong, message: String, stashId: Oid) -> CallbackResult

interface StashCallbackPayload {
    var stashCallback: StashCallback?
}

val staticStashCallback: git_stash_cb = staticCFunction {
        index: size_t, message: CPointer<ByteVar>?, id: CPointer<git_oid>?, payload: COpaquePointer?
    ->
    val callbackPayload = payload?.asStableRef<StashCallbackPayload>()?.get()
    callbackPayload?.stashCallback?.invoke(
        index,
        message?.toKString() ?: "",
        Oid(handler = id!!),
    )?.value ?: CallbackResult.Ok.value
}
