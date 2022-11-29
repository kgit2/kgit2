package com.kgit2.apply

import com.kgit2.common.error.GitErrorCode
import com.kgit2.diff.DiffDelta
import com.kgit2.diff.DiffHunk

typealias ApplyDeltaCallback = (delta: DiffDelta) -> GitErrorCode

typealias ApplyHunkCallback = (hunk: DiffHunk) -> GitErrorCode
