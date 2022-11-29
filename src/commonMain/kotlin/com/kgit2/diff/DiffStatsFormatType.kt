package com.kgit2.diff

import libgit2.*

enum class DiffStatsFormatType(val value: git_diff_stats_format_t) {
    None(GIT_DIFF_STATS_NONE),
    Full(GIT_DIFF_STATS_FULL),
    Short(GIT_DIFF_STATS_SHORT),
    Number(GIT_DIFF_STATS_NUMBER),
    IncludeSummary(GIT_DIFF_STATS_INCLUDE_SUMMARY),
    ;

    companion object {
        fun from(value: git_diff_stats_format_t): DiffStatsFormatType {
            return when (value) {
                GIT_DIFF_STATS_NONE -> None
                GIT_DIFF_STATS_FULL -> Full
                GIT_DIFF_STATS_SHORT -> Short
                GIT_DIFF_STATS_NUMBER -> Number
                GIT_DIFF_STATS_INCLUDE_SUMMARY -> IncludeSummary
                else -> throw IllegalArgumentException("Unknown value: $value")
            }
        }
    }
}
