package com.kgit2.merge

import com.kgit2.annotations.FlagMask
import libgit2.git_merge_file_favor_t

@FlagMask(
    flagsType = git_merge_file_favor_t::class,
    "GIT_MERGE_FILE_FAVOR_NORMAL",
    "GIT_MERGE_FILE_FAVOR_OURS",
    "GIT_MERGE_FILE_FAVOR_THEIRS",
    "GIT_MERGE_FILE_FAVOR_UNION",
)
data class MergeFileFavor(
    override var flags: git_merge_file_favor_t,
    override val onFlagsChanged: ((UInt) -> Unit)?,
) : MergeFileFavorMask<MergeFileFavor>
