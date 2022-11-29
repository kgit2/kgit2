package com.kgit2.diff

import com.kgit2.annotations.Raw
import com.kgit2.memory.GitBase
import kotlinx.cinterop.pointed
import libgit2.GIT_DIFF_FIND_OPTIONS_VERSION
import libgit2.git_diff_find_options
import libgit2.git_diff_find_options_init

/**
 * Control behavior of rename and copy detection
 * These options mostly mimic parameters that can be passed to git-diff.
 */
@Raw(
    base = git_diff_find_options::class
)
class DiffFindOptions(
    raw: DiffFindOptionsRaw = DiffFindOptionsRaw(initial = {
        git_diff_find_options_init(this, GIT_DIFF_FIND_OPTIONS_VERSION)
    })
) : GitBase<git_diff_find_options, DiffFindOptionsRaw>(raw) {

    /**
     * Combination of git_diff_find_t values (default GIT_DIFF_FIND_BY_CONFIG).
     * NOTE: if you don't explicitly set this, `diff.renames` could be set to false,
     * resulting in `git_diff_find_similar` doing nothing.
     */
    val flags: DiffFindFlags = DiffFindFlags(raw.handler.pointed.flags) {
        raw.handler.pointed.flags = it
    }

    /**
     * Threshold above which similar files will be considered renames.
     * This is equivalent to the -M option.
     * @default 50
     */
    var renameThreshold: UShort = raw.handler.pointed.rename_threshold

    /**
     * Threshold below which similar files will be eligible to be a rename source.
     * This is equivalent to the first part of the -B option.
     * @default 50
     */
    var renameFromRewriteThreshold: UShort = raw.handler.pointed.rename_from_rewrite_threshold

    /**
     * Threshold above which similar files will be considered copies.
     * This is equivalent to the -C option.
     * @default 50
     */
    var copyThreshold: UShort = raw.handler.pointed.copy_threshold

    /**
     * Threshold below which similar files will be split into a delete/add pair.
     * This is equivalent to the last part of the -B option.
     * @default 60
     */
    var breakRewriteThreshold: UShort = raw.handler.pointed.break_rewrite_threshold

    /**
     * Maximum number of matches to consider for a particular file.
     * This is a little different from the `-l` option from Git
     * because we will still process up to this many matches before abandoning the search.
     * @default 1000
     */
    var renameLimit: ULong = raw.handler.pointed.rename_limit

    /**
     * The `metric` option allows you to plug in a custom similarity metric.
     * Set it to NULL to use the default internal metric.
     * The default metric is based on sampling hashes of ranges of data in the file,
     * which is a pretty good similarity approximation that
     * should work fairly well for both text and binary data while still being pretty fast with a fixed memory overhead.
     */
    var metric: DiffSimilarityMetric? = null
}
