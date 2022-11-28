package com.kgit2.merge

import com.kgit2.annotations.FlagMask
import libgit2.git_merge_flag_t

@FlagMask(
    flagsType = git_merge_flag_t::class,
    "GIT_MERGE_FIND_RENAMES",
    "GIT_MERGE_FAIL_ON_CONFLICT",
    "GIT_MERGE_SKIP_REUC",
    "GIT_MERGE_VIRTUAL_BASE",
)
data class MergeFlag(
    override var flags: git_merge_flag_t,
    override val onFlagsChanged: (git_merge_flag_t) -> Unit,
) : MergeFlagMask<MergeFlag>
