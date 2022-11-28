package com.kgit2.merge

import com.kgit2.annotations.FlagMask
import libgit2.git_merge_file_flag_t

@FlagMask(
    flagsType = git_merge_file_flag_t::class,
    /**
     * Defaults
     */
    "GIT_MERGE_FILE_DEFAULT",

    /**
     * Create standard conflicted merge files
     */
    "GIT_MERGE_FILE_STYLE_MERGE",

    /**
     * Create diff3-style files
     */
    "GIT_MERGE_FILE_STYLE_DIFF3",

    /**
     * Condense non-alphanumeric regions for simplified diff file
     */
    "GIT_MERGE_FILE_SIMPLIFY_ALNUM",

    /**
     * Ignore all whitespace
     */
    "GIT_MERGE_FILE_IGNORE_WHITESPACE",

    /**
     * Ignore changes in amount of whitespace
     */
    "GIT_MERGE_FILE_IGNORE_WHITESPACE_CHANGE",

    /**
     * Ignore whitespace at end of line
     */
    "GIT_MERGE_FILE_IGNORE_WHITESPACE_EOL",

    /**
     * Use the "patience diff" algorithm
     */
    "GIT_MERGE_FILE_DIFF_PATIENCE",

    /**
     * Take extra time to find minimal diff
     */
    "GIT_MERGE_FILE_DIFF_MINIMAL",

    /**
     * Create zdiff3 ("zealous diff3")-style files
     */
    "GIT_MERGE_FILE_STYLE_ZDIFF3",

    /**
     * Do not produce file conflicts when common regions have changed; keep the conflict markers in the file and accept that as the merge result.
     */
    "GIT_MERGE_FILE_ACCEPT_CONFLICTS",
)
data class MergeFileFlag(
    override var flags: UInt,
    override val onFlagsChanged: ((UInt) -> Unit)?,
) : MergeFileFlagMask<MergeFileFlag>
