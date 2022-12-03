package com.kgit2.blame

import com.kgit2.annotations.FlagMask
import libgit2.git_blame_flag_t

@FlagMask(
    flagsType = git_blame_flag_t::class,
    /**
     * Normal blame, the default
     */
    "GIT_BLAME_NORMAL",

    /**
     * Track lines that have moved within a file (like git blame -M).This is not yet implemented and reserved for future use.
     */
    "GIT_BLAME_TRACK_COPIES_SAME_FILE",

    /**
     * Track lines that have moved across files in the same commit (like git blame -C).This is not yet implemented and reserved for future use.
     */
    "GIT_BLAME_TRACK_COPIES_SAME_COMMIT_MOVES",

    /**
     * Track lines that have been copied from another file that exists in the same commit (like git blame -CC). Implies SAME_FILE.This is not yet implemented and reserved for future use.
     */
    "GIT_BLAME_TRACK_COPIES_SAME_COMMIT_COPIES",

    /**
     * Track lines that have been copied from another file that exists in any commit (like git blame -CCC). Implies SAME_COMMIT_COPIES.This is not yet implemented and reserved for future use.
     */
    "GIT_BLAME_TRACK_COPIES_ANY_COMMIT_COPIES",

    /**
     * Restrict the search of commits to those reachable following only the first parents.
     */
    "GIT_BLAME_FIRST_PARENT",

    /**
     * Use mailmap file to map author and committer names and email addresses to canonical real names and email addresses. The mailmap will be read from the working directory, or HEAD in a bare repository.
     */
    "GIT_BLAME_USE_MAILMAP",

    /**
     * Ignore whitespace differences
     */
    "GIT_BLAME_IGNORE_WHITESPACE",
)
data class BlameFlag(
    override var flags: UInt,
    override val onFlagsChanged: ((UInt) -> Unit)?,
) : BlameFlagMask<BlameFlag>
