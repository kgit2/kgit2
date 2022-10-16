package com.floater.git.common.option

import libgit2.git_checkout_strategy_t

enum class CheckoutStrategyOpts(val value: git_checkout_strategy_t) {
    GIT_CHECKOUT_NONE(0u),
    /**< default is a dry run, no actual updates */

    /**
     * Allow safe updates that cannot overwrite uncommitted data.
     * If the uncommitted changes don't conflict with the checked out files,
     * the checkout will still proceed, leaving the changes intact.
     *
     * Mutually exclusive with GIT_CHECKOUT_FORCE.
     * GIT_CHECKOUT_FORCE takes precedence over GIT_CHECKOUT_SAFE.
     */
    GIT_CHECKOUT_SAFE(1u shl 0),

    /**
     * Allow all updates to force working directory to look like index.
     *
     * Mutually exclusive with GIT_CHECKOUT_SAFE.
     * GIT_CHECKOUT_FORCE takes precedence over GIT_CHECKOUT_SAFE.
     */
    GIT_CHECKOUT_FORCE(1u shl 1),


    /** Allow checkout to recreate missing files */
    GIT_CHECKOUT_RECREATE_MISSING(1u shl 2),

    /** Allow checkout to make safe updates even if conflicts are found */
    GIT_CHECKOUT_ALLOW_CONFLICTS(1u shl 4),

    /** Remove untracked files not in index (that are not ignored) */
    GIT_CHECKOUT_REMOVE_UNTRACKED(1u shl 5),

    /** Remove ignored files not in index */
    GIT_CHECKOUT_REMOVE_IGNORED(1u shl 6),

    /** Only update existing files, don't create new ones */
    GIT_CHECKOUT_UPDATE_ONLY(1u shl 7),

    /**
     * Normally checkout updates index entries as it goes; this stops that.
     * Implies `GIT_CHECKOUT_DONT_WRITE_INDEX`.
     */
    GIT_CHECKOUT_DONT_UPDATE_INDEX(1u shl 8),

    /** Don't refresh index/config/etc before doing checkout */
    GIT_CHECKOUT_NO_REFRESH(1u shl 9),

    /** Allow checkout to skip unmerged files */
    GIT_CHECKOUT_SKIP_UNMERGED(1u shl 10),

    /** For unmerged files, checkout stage 2 from index */
    GIT_CHECKOUT_USE_OURS(1u shl 11),

    /** For unmerged files, checkout stage 3 from index */
    GIT_CHECKOUT_USE_THEIRS(1u shl 12),

    /** Treat pathspec as simple list of exact match file paths */
    GIT_CHECKOUT_DISABLE_PATHSPEC_MATCH(1u shl 13),

    /** Ignore directories in use, they will be left empty */
    GIT_CHECKOUT_SKIP_LOCKED_DIRECTORIES(1u shl 18),

    /** Don't overwrite ignored files that exist in the checkout target */
    GIT_CHECKOUT_DONT_OVERWRITE_IGNORED(1u shl 19),

    /** Write normal merge files for conflicts */
    GIT_CHECKOUT_CONFLICT_STYLE_MERGE(1u shl 20),

    /** Include common ancestor data in diff3 format files for conflicts */
    GIT_CHECKOUT_CONFLICT_STYLE_DIFF3(1u shl 21),

    /** Don't overwrite existing files or folders */
    GIT_CHECKOUT_DONT_REMOVE_EXISTING(1u shl 22),

    /** Normally checkout writes the index upon completion; this prevents that. */
    GIT_CHECKOUT_DONT_WRITE_INDEX(1u shl 23),

    /**
     * Show what would be done by a checkout.  Stop after sending
     * notifications; don't update the working directory or index.
     */
    GIT_CHECKOUT_DRY_RUN(1u shl 24),

    /** Include common ancestor data in zdiff3 format for conflicts */
    GIT_CHECKOUT_CONFLICT_STYLE_ZDIFF3(1u shl 25),

    /**
     * THE FOLLOWING OPTIONS ARE NOT YET IMPLEMENTED
     */

    /** Recursively checkout submodules with same options (NOT IMPLEMENTED) */
    GIT_CHECKOUT_UPDATE_SUBMODULES(1u shl 16),

    /** Recursively checkout submodules if HEAD moved in super repo (NOT IMPLEMENTED) */
    GIT_CHECKOUT_UPDATE_SUBMODULES_IF_CHANGED(1u shl 17)
}
