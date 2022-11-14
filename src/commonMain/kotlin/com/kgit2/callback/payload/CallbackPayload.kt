package com.kgit2.callback.payload

import libgit2.git_indexer_progress

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
