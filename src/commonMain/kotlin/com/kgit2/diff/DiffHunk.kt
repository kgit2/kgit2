package com.kgit2.diff

import com.kgit2.annotations.Raw
import com.kgit2.common.error.GitErrorCode
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import kotlinx.cinterop.*
import libgit2.git_diff_hunk

typealias DiffHunkCallback = (delta: DiffDelta?, hunk: DiffHunk?) -> GitErrorCode

@Raw(
    base = git_diff_hunk::class,
)
class DiffHunk(raw: DiffHunkRaw) : GitBase<git_diff_hunk, DiffHunkRaw>(raw) {
    constructor(
        memory: Memory = Memory(),
        handler: DiffHunkPointer = memory.alloc<git_diff_hunk>().ptr,
        initial: DiffHunkInitial? = null
    ) : this(DiffHunkRaw(memory, handler, initial))

    val oldStart: Int = raw.handler.pointed.old_start

    val oldLines: Int = raw.handler.pointed.old_lines

    val newStart: Int = raw.handler.pointed.new_start

    val newLines: Int = raw.handler.pointed.new_lines

    val header: String = raw.handler.pointed.header.readBytes(raw.handler.pointed.header_len.convert() ?: 0).toKString()

    override fun toString(): String {
        return "DiffHunk(oldStart=$oldStart, oldLines=$oldLines, newStart=$newStart, newLines=$newLines, header='$header')"
    }
}
