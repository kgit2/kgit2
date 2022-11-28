package com.kgit2.status

import com.kgit2.annotations.FlagMask
import com.kgit2.common.option.BaseMultiple
import kotlinx.cinterop.convert
import libgit2.*

@FlagMask(
    flagsType = git_status_opt_t::class,
    /**
     * Says that callbacks should be made on untracked files.
     * These will only be made if the workdir files are included in the status
     * "show" option.
     */
    "GIT_STATUS_OPT_INCLUDE_UNTRACKED",

    /**
     * Says that ignored files get callbacks.
     * Again, these callbacks will only be made if the workdir files are
     * included in the status "show" option.
     */
    "GIT_STATUS_OPT_INCLUDE_IGNORED",

    /**
     * Indicates that callback should be made even on unmodified files.
     */
    "GIT_STATUS_OPT_INCLUDE_UNMODIFIED",

    /**
     * Indicates that submodules should be skipped.
     * This only applies if there are no pending typechanges to the submodule
     * (either from or to another type).
     */
    "GIT_STATUS_OPT_EXCLUDE_SUBMODULES",

    /**
     * Indicates that all files in untracked directories should be included.
     * Normally if an entire directory is new, then just the top-level
     * directory is included (with a trailing slash on the entry name).
     * This flag says to include all of the individual files in the directory
     * instead.
     */
    "GIT_STATUS_OPT_RECURSE_UNTRACKED_DIRS",

    /**
     * Indicates that the given path should be treated as a literal path,
     * and not as a pathspec pattern.
     */
    "GIT_STATUS_OPT_DISABLE_PATHSPEC_MATCH",

    /**
     * Indicates that the contents of ignored directories should be included
     * in the status. This is like doing `git ls-files -o -i --exclude-standard`
     * with core git.
     */
    "GIT_STATUS_OPT_RECURSE_IGNORED_DIRS",

    /**
     * Indicates that rename detection should be processed between the head and
     * the index and enables the GIT_STATUS_INDEX_RENAMED as a possible status
     * flag.
     */
    "GIT_STATUS_OPT_RENAMES_HEAD_TO_INDEX",

    /**
     * Indicates that rename detection should be run between the index and the
     * working directory and enabled GIT_STATUS_WT_RENAMED as a possible status
     * flag.
     */
    "GIT_STATUS_OPT_RENAMES_INDEX_TO_WORKDIR",

    /**
     * Overrides the native case sensitivity for the file system and forces
     * the output to be in case-sensitive order.
     */
    "GIT_STATUS_OPT_SORT_CASE_SENSITIVELY",

    /**
     * Overrides the native case sensitivity for the file system and forces
     * the output to be in case-insensitive order.
     */
    "GIT_STATUS_OPT_SORT_CASE_INSENSITIVELY",

    /**
     * Iindicates that rename detection should include rewritten files.
     */
    "GIT_STATUS_OPT_RENAMES_FROM_REWRITES",

    /**
     * Bypasses the default status behavior of doing a "soft" index reload
     * (i.e. reloading the index data if the file on disk has been modified
     * outside libgit2).
     */
    "GIT_STATUS_OPT_NO_REFRESH",

    /**
     * Tells libgit2 to refresh the stat cache in the index for files that are
     * unchanged but have out of date stat einformation in the index.
     * It will result in less work being done on subsequent calls to get status.
     * This is mutually exclusive with the NO_REFRESH option.
     */
    "GIT_STATUS_OPT_UPDATE_INDEX",

    /**
     * Normally files that cannot be opened or read are ignored as
     * these are often transient files; this option will return
     * unreadable files as `GIT_STATUS_WT_UNREADABLE`.
     */
    "GIT_STATUS_OPT_INCLUDE_UNREADABLE",

    /**
     * Unreadable files will be detected and given the status
     * untracked instead of unreadable.
     */
    "GIT_STATUS_OPT_INCLUDE_UNREADABLE_AS_UNTRACKED",
)
data class StatusOptionsFlag(
    override var flags: UInt,
    override val onFlagsChanged: ((UInt) -> Unit)?,
) : StatusOptionsFlagMask<StatusOptionsFlag>
