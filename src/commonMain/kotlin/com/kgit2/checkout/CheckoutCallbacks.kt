package com.kgit2.checkout

import com.kgit2.common.callback.CallbackResult
import com.kgit2.common.memory.Memory
import com.kgit2.diff.DiffFile
import com.kgit2.index.CheckoutPerf
import com.kgit2.index.IndexerProgress
import kotlinx.cinterop.*
import libgit2.*
import platform.posix.size_t

/**
 * Type for progress callbacks during indexing.  Return a value less
 * than zero to cancel the indexing or download.
 *
 * @param progress Structure containing information about the state of the transfer
 */
typealias IndexerProgressCallback = (progress: IndexerProgress) -> CallbackResult

interface IndexerProgressCallbackPayload {
    var indexerProgressCallback: IndexerProgressCallback?
}

val staticIndexerProgressCallback: git_indexer_progress_cb = staticCFunction { stats, payload ->
    val callback = payload!!.asStableRef<IndexerProgressCallbackPayload>().get()
    val progress = IndexerProgress(
        stats!!.pointed.total_objects,
        stats.pointed.indexed_objects,
        stats.pointed.received_objects,
        stats.pointed.local_objects,
        stats.pointed.total_deltas,
        stats.pointed.indexed_deltas,
        stats.pointed.received_bytes
    )
    callback.indexerProgressCallback?.invoke(progress)?.value ?: CallbackResult.Ok.value
}

typealias CheckoutProgressCallback = (path: String?, completedSteps: ULong, totalSteps: ULong) -> Unit

interface CheckoutProgressCallbackPayload {
    var checkoutProgressCallback: CheckoutProgressCallback?
}

val staticCheckoutProgressCallback: git_checkout_progress_cb =
    staticCFunction {
            path: CPointer<ByteVar>?,
            completedSteps: size_t,
            totalSteps: size_t,
            payload: COpaquePointer?,
        ->
        val callback = payload!!.asStableRef<CheckoutProgressCallbackPayload>().get()
        callback.checkoutProgressCallback!!.invoke(
            path?.toKString(),
            completedSteps,
            totalSteps
        )
    }

typealias CheckoutNotifyCallback = (type: CheckoutNotificationType, path: String?, baseline: DiffFile?, target: DiffFile?, workdir: DiffFile?) -> CallbackResult

interface CheckoutNotifyCallbackPayload {
    var checkoutNotifyCallback: CheckoutNotifyCallback?
}

val staticCheckoutNotifyCallback: git_checkout_notify_cb =
    staticCFunction {
            why: git_checkout_notify_t,
            path: CPointer<ByteVar>?,
            baseline: CPointer<git_diff_file>?,
            target: CPointer<git_diff_file>?,
            workdir: CPointer<git_diff_file>?,
            payload: COpaquePointer?,
        ->
        val callback = payload!!.asStableRef<CheckoutNotifyCallbackPayload>().get()
        callback.checkoutNotifyCallback!!.invoke(
            CheckoutNotificationType.fromRaw(why),
            path?.toKString(),
            DiffFile(Memory(), baseline!!),
            DiffFile(Memory(), target!!),
            DiffFile(Memory(), workdir!!),
        ).value
    }

typealias CheckoutPerfCallback = (perfdata: CheckoutPerf) -> Unit

interface CheckoutPerfCallbackPayload {
    var checkoutPerfCallback: CheckoutPerfCallback?
}

val staticCheckoutPerfCallback: git_checkout_perfdata_cb =
    staticCFunction {
            perfdata: CPointer<git_checkout_perfdata>?,
            payload: COpaquePointer?,
        ->
        val callback = payload!!.asStableRef<CheckoutPerfCallbackPayload>().get()
        callback.checkoutPerfCallback!!.invoke(
            CheckoutPerf(
                perfdata!!.pointed.mkdir_calls,
                perfdata.pointed.stat_calls,
                perfdata.pointed.chmod_calls
            )
        )
    }
