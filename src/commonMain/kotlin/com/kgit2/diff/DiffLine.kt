package com.kgit2.diff

import com.kgit2.annotations.Raw
import com.kgit2.common.error.GitErrorCode
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import kotlinx.cinterop.*
import libgit2.git_diff_line

typealias DiffLineCallback = (delta: DiffDelta?, hunk: DiffHunk?, line: DiffLine?) -> GitErrorCode

@Raw(
    base = git_diff_line::class,
)
class DiffLine(raw: DiffLineRaw) : GitBase<git_diff_line, DiffLineRaw>(raw) {
    constructor(
        memory: Memory = Memory(),
        handler: DiffLinePointer = memory.alloc<git_diff_line>().ptr,
        initial: DiffLineInitial? = null
    ) : this(DiffLineRaw(memory, handler, initial))

    val origin: DiffLineOrigin = DiffLineOrigin.fromByte(raw.handler.pointed.origin)

    val content: String? = raw.handler.pointed.content?.readBytes(raw.handler.pointed.content_len.convert() ?: 0)?.toKString()

    val contentOffset: Long = raw.handler.pointed.content_offset

    val oldLineNo: Int = raw.handler.pointed.old_lineno

    val newLineNo: Int = raw.handler.pointed.new_lineno

    val numLines: Int = raw.handler.pointed.num_lines

    override fun toString(): String {
        return "DiffLine(origin=$origin, content='$content', oldLineno=$oldLineNo, newLineno=$newLineNo, numLines=$numLines)"
    }
}
