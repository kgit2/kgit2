package com.kgit2.blame

import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.memory.RawWrapper
import com.kgit2.oid.Oid
import com.kgit2.signature.Signature
import kotlinx.cinterop.pointed
import kotlinx.cinterop.toKString
import libgit2.git_blame_hunk

@Raw(
    base = git_blame_hunk::class,
)
class BlameHunk(raw: BlameHunkRaw) : RawWrapper<git_blame_hunk, BlameHunkRaw>(raw) {
    constructor(handler: BlameHunkPointer) : this(BlameHunkRaw(Memory(), handler))

    constructor(secondaryInitial: BlameHunkSecondaryInitial) : this(BlameHunkRaw(secondaryInitial = secondaryInitial))

    val linesCount: ULong = raw.handler.pointed.lines_in_hunk

    val finalCommitId: Oid = Oid(Memory(), raw.handler.pointed.final_commit_id)

    val finalSignature: Signature = Signature(Memory(), raw.handler.pointed.final_signature!!)

    val originCommitID: Oid = Oid(Memory(), raw.handler.pointed.orig_commit_id)

    val originPath: String = raw.handler.pointed.orig_path!!.toKString()

    val originStartLineNumber: ULong = raw.handler.pointed.orig_start_line_number

    val originSignature: Signature = Signature(Memory(), raw.handler.pointed.orig_signature!!)

    val isBoundary: Boolean = raw.handler.pointed.boundary == 1.toByte()
}
