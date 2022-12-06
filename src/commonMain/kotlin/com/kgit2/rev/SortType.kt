package com.kgit2.rev

import libgit2.GIT_SORT_NONE
import libgit2.GIT_SORT_REVERSE
import libgit2.GIT_SORT_TIME
import libgit2.GIT_SORT_TOPOLOGICAL
import libgit2.git_sort_t

enum class SortType(val value: git_sort_t) {
    /**
     * Sort the output with the same default method from git: reverse chronological order. This is the default sorting for new walkers.
     */
    None(GIT_SORT_NONE),

    /**
     * Sort the repository contents in topological order (no parents before all of its children are shown); this sorting mode can be combined with time sorting to produce git's --date-order`.
     */
    Topological(GIT_SORT_TOPOLOGICAL),

    /**
     * Sort the repository contents by commit time; this sorting mode can be combined with topological sorting.
     */
    Time(GIT_SORT_TIME),

    /**
     * Iterate through the repository contents in reverse order; this sorting mode can be combined with any of the above.
     */
    Reverse(GIT_SORT_REVERSE),
    ;

    companion object {
        fun from(value: git_sort_t): SortType {
            return when (value) {
                GIT_SORT_NONE -> None
                GIT_SORT_TOPOLOGICAL -> Topological
                GIT_SORT_TIME -> Time
                GIT_SORT_REVERSE -> Reverse
                else -> throw IllegalArgumentException("Unknown value: $value")
            }
        }
    }
}
