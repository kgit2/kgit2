package com.kgit2.diff

import cnames.structs.git_diff
import com.kgit2.common.error.GitErrorCode
import com.kgit2.common.memory.Memory
import kotlinx.cinterop.*
import libgit2.*

typealias DiffProgressCallback = (diffSoFar: Diff, oldPath: String?, newPath: String?) -> GitErrorCode

interface DiffProgressCallbackPayload {
    var diffProgressCallback: DiffProgressCallback?
}

val staticDiffProgressCallback: git_diff_progress_cb = staticCFunction {
        diffSoFar: CPointer<git_diff>?,
        oldPath: CPointer<ByteVar>?,
        newPath: CPointer<ByteVar>?,
        payload: COpaquePointer?,
    ->
    val callback = payload!!.asStableRef<DiffProgressCallbackPayload>().get()
    callback.diffProgressCallback!!.invoke(
        Diff(DiffRaw(Memory(), diffSoFar!!)),
        oldPath?.toKString(),
        newPath?.toKString()
    ).value
}

typealias DiffNotifyCallback = (diffSoFar: Diff, deltaToAdd: DiffDelta, matchedPathSpec: String?) -> GitErrorCode

interface DiffNotifyCallbackPayload {
    var diffNotifyCallback: DiffNotifyCallback?
}

val staticDiffNotifyCallback: git_diff_notify_cb = staticCFunction {
        diffSoFar: CPointer<git_diff>?,
        deltaToAdd: CPointer<git_diff_delta>?,
        matchedPathSpec: CPointer<ByteVar>?,
        payload: COpaquePointer?,
    ->
    val callback = payload!!.asStableRef<DiffNotifyCallbackPayload>().get()
    callback.diffNotifyCallback!!.invoke(
        Diff(DiffRaw(Memory(), diffSoFar!!)),
        DiffDelta(DiffDeltaRaw(Memory(), deltaToAdd!!)),
        matchedPathSpec?.toKString()
    ).value
}

typealias DiffFileCallback = (delta: DiffDelta?, progress: Float?) -> GitErrorCode

interface DiffFileCallbackPayload {
    var diffFileCallback: DiffFileCallback?
}

val staticDiffFileCallback: git_diff_file_cb = staticCFunction {
        delta: CPointer<git_diff_delta>?,
        progress: Float,
        payload: COpaquePointer?,
    ->
    val callback = payload!!.asStableRef<DiffFileCallbackPayload>().get()
    callback.diffFileCallback!!.invoke(
        delta?.let { DiffDelta(handler = it) },
        progress
    ).value
}

typealias DiffBinaryCallback = (delta: DiffDelta?, oldFile: DiffBinary?) -> GitErrorCode

interface DiffBinaryCallbackPayload {
    var diffBinaryCallback: DiffBinaryCallback?
}

val staticDiffBinaryCallback: git_diff_binary_cb = staticCFunction {
        delta: CPointer<git_diff_delta>?,
        oldFile: CPointer<git_diff_binary>?,
        payload: COpaquePointer?,
    ->
    val callback = payload!!.asStableRef<DiffBinaryCallbackPayload>().get()
    callback.diffBinaryCallback!!.invoke(
        delta?.let { DiffDelta(handler = it) },
        oldFile?.let { DiffBinary(handler = it) },
    ).value
}

typealias DiffHunkCallback = (delta: DiffDelta?, hunk: DiffHunk?) -> GitErrorCode

interface DiffHunkCallbackPayload {
    var diffHunkCallback: DiffHunkCallback?
}

val staticDiffHunkCallback: git_diff_hunk_cb = staticCFunction {
        delta: CPointer<git_diff_delta>?,
        hunk: CPointer<git_diff_hunk>?,
        payload: COpaquePointer?,
    ->
    val callback = payload!!.asStableRef<DiffHunkCallbackPayload>().get()
    callback.diffHunkCallback!!.invoke(
        delta?.let { DiffDelta(handler = it) },
        hunk?.let { DiffHunk(handler = it) },
    ).value
}

typealias DiffLineCallback = (delta: DiffDelta?, hunk: DiffHunk?, line: DiffLine?) -> GitErrorCode

interface DiffLineCallbackPayload {
    var diffLineCallback: DiffLineCallback?
}

val staticDiffLineCallback: git_diff_line_cb = staticCFunction {
        delta: CPointer<git_diff_delta>?,
        hunk: CPointer<git_diff_hunk>?,
        line: CPointer<git_diff_line>?,
        payload: COpaquePointer?,
    ->
    val callback = payload!!.asStableRef<DiffLineCallbackPayload>().get()
    callback.diffLineCallback!!.invoke(
        delta?.let { DiffDelta(handler = it) },
        hunk?.let { DiffHunk(handler = it) },
        line?.let { DiffLine(handler = it) },
    ).value
}
