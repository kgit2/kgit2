package com.kgit2.diff

import cnames.structs.git_diff
import com.kgit2.common.callback.CallbackResult
import com.kgit2.common.memory.Memory
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.toKString
import libgit2.git_diff_binary
import libgit2.git_diff_binary_cb
import libgit2.git_diff_delta
import libgit2.git_diff_file_cb
import libgit2.git_diff_hunk
import libgit2.git_diff_hunk_cb
import libgit2.git_diff_line
import libgit2.git_diff_line_cb
import libgit2.git_diff_notify_cb
import libgit2.git_diff_progress_cb

typealias DiffProgressCallback = (diffSoFar: Diff, oldPath: String?, newPath: String?) -> CallbackResult

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

typealias DiffNotifyCallback = (diffSoFar: Diff, deltaToAdd: DiffDelta, matchedPathSpec: String?) -> CallbackResult

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

typealias DiffFileCallback = (delta: DiffDelta?, progress: Float?) -> CallbackResult

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

typealias DiffBinaryCallback = (delta: DiffDelta?, oldFile: DiffBinary?) -> CallbackResult

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

typealias DiffHunkCallback = (delta: DiffDelta?, hunk: DiffHunk?) -> CallbackResult

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

typealias DiffLineCallback = (delta: DiffDelta?, hunk: DiffHunk?, line: DiffLine?) -> CallbackResult

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
