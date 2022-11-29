package com.kgit2.checkout

import com.kgit2.common.error.GitErrorCode
import com.kgit2.diff.DiffFile
import com.kgit2.index.CheckoutPerf
import com.kgit2.index.IndexerProgress

/**
 * Type for progress callbacks during indexing.  Return a value less
 * than zero to cancel the indexing or download.
 *
 * @param progress Structure containing information about the state of the transfer
 */
typealias IndexerProgressCallback = (progress: IndexerProgress) -> GitErrorCode

typealias CheckoutProgressCallback = (path: String, completedSteps: ULong, totalSteps: ULong) -> Unit

typealias CheckoutNotifyCallback = (type: CheckoutNotificationType, path: String?, baseline: DiffFile?, target: DiffFile?, workdir: DiffFile?) -> GitErrorCode

typealias CheckoutPerfCallback = (perfdata: CheckoutPerf) -> Unit
