package com.kgit2.push

import com.kgit2.common.error.GitErrorCode
import com.kgit2.oid.Oid

/**
 * Type definition for push transfer progress callbacks.
 *
 * This type is deprecated, but there is no plan to remove this
 * type definition at this time.
 */
typealias PushTransferProgressCallback = (current: UInt, total: UInt, bytes: ULong) -> GitErrorCode

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

/**
 * Callback for the user's custom push negotiation.
 *
 * @param updates The list of updates to be sent to the remote
 * @return 0 to proceed with the push, < 0 to fail the push
 */
typealias PushNegotiationCallback = (updates: List<PushUpdate>) -> GitErrorCode

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
