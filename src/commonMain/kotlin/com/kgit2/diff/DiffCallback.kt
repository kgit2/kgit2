package com.kgit2.diff

import com.kgit2.common.error.GitErrorCode

typealias DiffProgressCallback = (diffSoFar: Diff, oldPath: String?, newPath: String?) -> GitErrorCode

typealias DiffNotifyCallback = (diffSoFar: Diff, deltaToAdd: DiffDelta, matchedPathSpec: String?) -> GitErrorCode
