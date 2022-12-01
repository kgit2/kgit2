package com.kgit2.push

import com.kgit2.common.error.GitErrorCode
import com.kgit2.common.memory.Memory
import com.kgit2.oid.Oid
import kotlinx.cinterop.*
import libgit2.git_push_negotiation
import libgit2.git_push_transfer_progress_cb
import libgit2.git_push_update
import libgit2.git_push_update_reference_cb
import platform.posix.size_t

/**
 * Type definition for push transfer progress callbacks.
 *
 * This type is deprecated, but there is no plan to remove this
 * type definition at this time.
 */
typealias PushTransferProgressCallback = (current: UInt, total: UInt, bytes: ULong) -> GitErrorCode

interface PushTransferProgressCallbackPayload {
    var pushTransferProgressCallback: PushTransferProgressCallback?
}

val staticPushTransferProgressCallback: git_push_transfer_progress_cb = staticCFunction {
        current: UInt,
        total: UInt,
        bytes: size_t,
        payload: COpaquePointer?,
    ->
    val callback = payload?.asStableRef<PushTransferProgressCallbackPayload>()?.get()
    callback?.pushTransferProgressCallback?.invoke(current, total, bytes)?.value ?: GitErrorCode.Ok.value
}

/**
 * Callback used to inform of the update status from the remote.
 *
 * Called for each updated reference on push. If `status` is
 * not `NULL`, the update was rejected by the remote server
 * and `status` contains the reason given.
 *
 * @param refname refname specifying to the remote ref
 * @param status status message sent from the remote
 * @return GIT_OK on success, otherwise an error
 */
typealias PushUpdateReferenceCallback = (refname: String, status: String) -> GitErrorCode

interface PushUpdateReferenceCallbackPayload {
    var pushUpdateReferenceCallback: PushUpdateReferenceCallback?
}

val staticPushUpdateReferenceCallback: git_push_update_reference_cb = staticCFunction {
        refname: CPointer<ByteVar>?,
        status: CPointer<ByteVar>?,
        payload: COpaquePointer?,
    ->
    val callback = payload?.asStableRef<PushUpdateReferenceCallbackPayload>()?.get()
    callback?.pushUpdateReferenceCallback?.invoke(refname!!.toKString(), status!!.toKString())?.value
        ?: GitErrorCode.Ok.value
}

/**
 * Callback for the user's custom push negotiation.
 *
 * @param updates The list of updates to be sent to the remote
 * @return 0 to proceed with the push, < 0 to fail the push
 */
typealias PushNegotiationCallback = (updates: List<PushUpdate>) -> GitErrorCode

interface PushNegotiationCallbackPayload {
    var pushNegotiationCallback: PushNegotiationCallback?
}

val staticPushNegotiationCallback: git_push_negotiation = staticCFunction {
        updates: CPointer<CPointerVar<git_push_update>>?,
        len: size_t,
        payload: COpaquePointer?,
    ->
    val updateList = MutableList(len.toInt()) {
        val update = updates!![it]!!.pointed
        PushUpdate(
            update.src_refname!!.toKString(),
            update.dst_refname!!.toString(),
            Oid(Memory(), update.src.ptr),
            Oid(Memory(), update.dst.ptr)
        )
    }
    val callback = payload?.asStableRef<PushNegotiationCallbackPayload>()?.get()
    callback?.pushNegotiationCallback?.invoke(updateList)?.value ?: GitErrorCode.Ok.value
}

interface PushTransferProgress {
    fun pushTransferProgress(
        current: Int,
        total: Int,
        bytes: Int,
    )
}

data class PushUpdate(
    val srcRefName: String,
    val dstRefName: String,
    val src: Oid,
    val dst: Oid,
)
