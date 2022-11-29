package com.kgit2.diff

import com.kgit2.annotations.FlagMask
import libgit2.git_diff_find_t

@FlagMask(
    flagsType = git_diff_find_t::class,
    /**
     * Obey diff.renames. Overridden by any other GIT_DIFF_FIND_... flag.
     */
    "GIT_DIFF_FIND_BY_CONFIG",
    /**
     * Look for renames? (--find-renames)
     */
    "GIT_DIFF_FIND_RENAMES",
    /**
     * Consider old side of MODIFIED for renames? (--break-rewrites=N)
     */
    "GIT_DIFF_FIND_RENAMES_FROM_REWRITES",
    /**
     * Look for copies? (a la --find-copies).
     */
    "GIT_DIFF_FIND_COPIES",
    /**
     * Consider UNMODIFIED as copy sources? (--find-copies-harder).
     * For this to work correctly,
     * use GIT_DIFF_INCLUDE_UNMODIFIED when the initial git_diff is being generated.
     */
    "GIT_DIFF_FIND_COPIES_FROM_UNMODIFIED",
    /**
     * Mark significant rewrites for split (--break-rewrites=/M)
     */
    "GIT_DIFF_FIND_REWRITES",
    /**
     * Actually split large rewrites into delete/add pairs
     */
    "GIT_DIFF_BREAK_REWRITES",
    /**
     * Mark rewrites for split and break into delete/add pairs
     */
    "GIT_DIFF_FIND_AND_BREAK_REWRITES",
    /**
     * Find renames/copies for UNTRACKED items in working directory.For this to work correctly,
     * use GIT_DIFF_INCLUDE_UNTRACKED when the initial git_diff is being generated
     * (and obviously the diff must be against the working directory for this to make sense).
     */
    "GIT_DIFF_FIND_FOR_UNTRACKED",
    /**
     * Turn on all finding features.
     */
    "GIT_DIFF_FIND_ALL",
    /**
     * Measure similarity ignoring leading whitespace (default)
     */
    "GIT_DIFF_FIND_IGNORE_LEADING_WHITESPACE",
    /**
     * Measure similarity ignoring all whitespace
     */
    "GIT_DIFF_FIND_IGNORE_WHITESPACE",
    /**
     * Measure similarity including all data
     */
    "GIT_DIFF_FIND_DONT_IGNORE_WHITESPACE",
    /**
     * Measure similarity only by comparing SHAs (fast and cheap)
     */
    "GIT_DIFF_FIND_EXACT_MATCH_ONLY",
    /**
     * Do not break rewrites unless they contribute to a rename.Normally,
     * GIT_DIFF_FIND_AND_BREAK_REWRITES
     * will measure the self- similarity of modified files
     * and split the ones that have changed a lot into a DELETE / ADD pair.
     * Then the sides of that pair will be considered candidates for rename and copy detection.
     * If you add this flag in and the split pair is not used for an actual rename or copy,
     * then the modified record will be restored to a regular MODIFIED record instead of being split.
     */
    "GIT_DIFF_BREAK_REWRITES_FOR_RENAMES_ONLY",
    /**
     * Remove any UNMODIFIED deltas after find_similar is done.
     * Using GIT_DIFF_FIND_COPIES_FROM_UNMODIFIED to emulate the
     * --find-copies-harder behavior requires building a diff with the GIT_DIFF_INCLUDE_UNMODIFIED flag.
     * If you do not want UNMODIFIED records in the final result,
     * pass this flag to have them removed.
     */
    "GIT_DIFF_FIND_REMOVE_UNMODIFIED",
)
data class DiffFindFlags(
    override var flags: UInt,
    override val onFlagsChanged: ((UInt) -> Unit)?,
) : DiffFindFlagsMask<DiffFindFlags>
