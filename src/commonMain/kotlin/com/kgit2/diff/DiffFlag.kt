package com.kgit2.diff

import com.kgit2.common.option.BaseMultiple
import libgit2.*

data class DiffFlag(val value: git_diff_flag_t) : BaseMultiple<DiffFlag>() {
    companion object {
        /**< file(s) treated as binary data */
        val Binary = DiffFlag(GIT_DIFF_FLAG_BINARY)

        /**< file(s) treated as text data */
        val NotBinary = DiffFlag(GIT_DIFF_FLAG_NOT_BINARY)

        /**< `id` value is known correct */
        val ValidID = DiffFlag(GIT_DIFF_FLAG_VALID_ID)

        /**< file exists at this side of the delta */
        val Exists = DiffFlag(GIT_DIFF_FLAG_EXISTS)

        /**< file size value is known correct */
        val ValidSize = DiffFlag(GIT_DIFF_FLAG_VALID_SIZE)
    }

    override val longValue: ULong
        get() = value.toULong()
}
