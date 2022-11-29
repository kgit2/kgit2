package com.kgit2.index

import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.toKString
import libgit2.git_index_matched_path_cb
import libgit2.git_indexer_progress

interface IndexMatchedPathCallback {
    fun matchedPath(pathspec: String, path: String): Int

    fun toRawCB(): git_index_matched_path_cb = staticCFunction { path, matchedPathSpec, payload ->
        val callbackPayload = payload!!.asStableRef<IndexMatchedPathCallback>()
        val result = callbackPayload.get().matchedPath(path!!.toKString(), matchedPathSpec!!.toKString())
        callbackPayload.dispose()
        result
    }
}

data class IndexerProgress(
    val totalObjects: UInt,
    val indexedObjects: UInt,
    val receivedObjects: UInt,
    val localObjects: UInt,
    val totalDeltas: UInt,
    val indexedDeltas: UInt,
    val receivedBytes: ULong,
) {
    companion object {
        fun fromHandler(handler: git_indexer_progress): IndexerProgress {
            return IndexerProgress(
                handler.total_objects,
                handler.indexed_objects,
                handler.received_objects,
                handler.local_objects,
                handler.total_deltas,
                handler.indexed_deltas,
                handler.received_bytes,
            )
        }
    }
}

data class CheckoutPerf(
    val mkdirCalls: ULong,
    val statCalls: ULong,
    val chmodCalls: ULong,
)
