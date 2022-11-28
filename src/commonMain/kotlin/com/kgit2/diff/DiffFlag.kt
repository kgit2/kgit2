package com.kgit2.diff

import com.kgit2.annotations.FlagMask
import libgit2.git_diff_flag_t

@FlagMask(
    flagsType = git_diff_flag_t::class,
    "GIT_DIFF_FLAG_BINARY",
    "GIT_DIFF_FLAG_NOT_BINARY",
    "GIT_DIFF_FLAG_VALID_ID",
    "GIT_DIFF_FLAG_EXISTS",
    "GIT_DIFF_FLAG_VALID_SIZE",
    flagsMutable = false,
)
data class DiffFlag(
    override var flags: UInt,
) : DiffFlagMask<DiffFlag>
