package com.kgit2.diff

import libgit2.GIT_DIFF_LINE_ADDITION
import libgit2.GIT_DIFF_LINE_ADD_EOFNL
import libgit2.GIT_DIFF_LINE_BINARY
import libgit2.GIT_DIFF_LINE_CONTEXT
import libgit2.GIT_DIFF_LINE_CONTEXT_EOFNL
import libgit2.GIT_DIFF_LINE_DELETION
import libgit2.GIT_DIFF_LINE_DEL_EOFNL
import libgit2.GIT_DIFF_LINE_FILE_HDR
import libgit2.GIT_DIFF_LINE_HUNK_HDR
import libgit2.git_diff_line_t

enum class DiffLineOrigin(val value: git_diff_line_t) {
    Context(GIT_DIFF_LINE_CONTEXT),
    Addition(GIT_DIFF_LINE_ADDITION),
    Deletion(GIT_DIFF_LINE_DELETION),

    /**
     * Both files have no LF at end
     */
    ContextEOFNL(GIT_DIFF_LINE_CONTEXT_EOFNL),

    /**
     * Old has no LF at end, new does
     */
    AddEOFNL(GIT_DIFF_LINE_ADD_EOFNL),

    /**
     * Old has LF at end, new does not
     */
    DelEOFNL(GIT_DIFF_LINE_DEL_EOFNL),

    FileHDR(GIT_DIFF_LINE_FILE_HDR),

    HundHDR(GIT_DIFF_LINE_HUNK_HDR),

    /**
     * For "Binary files x and y differ"
     */
    Binary(GIT_DIFF_LINE_BINARY),
    ;

    companion object {
        fun from(value: git_diff_line_t): DiffLineOrigin {
            return when (value) {
                GIT_DIFF_LINE_CONTEXT -> Context
                GIT_DIFF_LINE_ADDITION -> Addition
                GIT_DIFF_LINE_DELETION -> Deletion
                GIT_DIFF_LINE_CONTEXT_EOFNL -> ContextEOFNL
                GIT_DIFF_LINE_ADD_EOFNL -> AddEOFNL
                GIT_DIFF_LINE_DEL_EOFNL -> DelEOFNL
                GIT_DIFF_LINE_FILE_HDR -> FileHDR
                GIT_DIFF_LINE_HUNK_HDR -> HundHDR
                GIT_DIFF_LINE_BINARY -> Binary
                else -> throw IllegalArgumentException("Unknown value: $value")
            }
        }

        fun fromByte(value: Byte): DiffLineOrigin = from(value.toUInt())
    }

    fun toChar(): Char = when (this) {
        Context -> ' '
        Addition -> '+'
        Deletion -> '-'
        ContextEOFNL -> '='
        AddEOFNL -> '>'
        DelEOFNL -> '<'
        FileHDR -> 'F'
        HundHDR -> 'H'
        Binary -> 'B'
    }
}
