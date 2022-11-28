package com.kgit2.merge

import com.kgit2.annotations.FlagMask
import libgit2.git_merge_analysis_t

@FlagMask(
    flagsType = git_merge_analysis_t::class,
    "GIT_MERGE_ANALYSIS_NONE",
    "GIT_MERGE_ANALYSIS_NORMAL",
    "GIT_MERGE_ANALYSIS_UP_TO_DATE",
    "GIT_MERGE_ANALYSIS_FASTFORWARD",
    "GIT_MERGE_ANALYSIS_UNBORN",
    flagsMutable = false,
)
data class MergeAnalysisFlag(
    override var flags: git_merge_analysis_t,
) : MergeAnalysisFlagMask<MergeAnalysisFlag>
