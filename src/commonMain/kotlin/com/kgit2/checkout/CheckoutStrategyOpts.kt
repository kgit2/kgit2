package com.kgit2.checkout

import com.kgit2.common.option.BaseMultiple
import libgit2.*

data class CheckoutStrategyOpts(val value: git_checkout_strategy_t) : BaseMultiple<CheckoutStrategyOpts>() {
    companion object {
        val None = CheckoutStrategyOpts(GIT_CHECKOUT_NONE)
        /**< default is a dry run, no actual updates */

        /**
         * Allow safe updates that cannot overwrite uncommitted data.
         * If the uncommitted changes don't conflict with the checked out files,
         * the checkout will still proceed, leaving the changes intact.
         *
         * Mutually exclusive with GIT_CHECKOUT_FORCE.
         * GIT_CHECKOUT_FORCE takes precedence over GIT_CHECKOUT_SAFE.
         */
        val Safe = CheckoutStrategyOpts(GIT_CHECKOUT_SAFE)

        /**
         * Allow all updates to force working directory to look like index.
         *
         * Mutually exclusive with GIT_CHECKOUT_SAFE.
         * GIT_CHECKOUT_FORCE takes precedence over GIT_CHECKOUT_SAFE.
         */
        val Force = CheckoutStrategyOpts(GIT_CHECKOUT_FORCE)

        /** Allow checkout to recreate missing files */
        val ReCreateMissing = CheckoutStrategyOpts(GIT_CHECKOUT_RECREATE_MISSING)

        /** Allow checkout to make safe updates even if conflicts are found */
        val AllowConflicts = CheckoutStrategyOpts(GIT_CHECKOUT_ALLOW_CONFLICTS)

        /** Remove untracked files not in index (that are not ignored) */
        val RemoveUnTracked = CheckoutStrategyOpts(GIT_CHECKOUT_REMOVE_UNTRACKED)

        /** Remove ignored files not in index */
        val RemoveIgnored = CheckoutStrategyOpts(GIT_CHECKOUT_REMOVE_IGNORED)

        /** Only update existing files, don't create new ones */
        val UpdateOnly = CheckoutStrategyOpts(GIT_CHECKOUT_UPDATE_ONLY)

        /**
         * Normally checkout updates index entries as it goes; this stops that.
         * Implies `GIT_CHECKOUT_DONT_WRITE_INDEX`.
         */
        val DontUpdateIndex = CheckoutStrategyOpts(GIT_CHECKOUT_DONT_UPDATE_INDEX)

        /** Don't refresh index/config/etc before doing checkout */
        val NoRefresh = CheckoutStrategyOpts(GIT_CHECKOUT_NO_REFRESH)

        /** Allow checkout to skip unmerged files */
        val SkipUnMerged = CheckoutStrategyOpts(GIT_CHECKOUT_SKIP_UNMERGED)

        /** For unmerged files, checkout stage 2 from index */
        val UserOurs = CheckoutStrategyOpts(GIT_CHECKOUT_USE_OURS)

        /** For unmerged files, checkout stage 3 from index */
        val UserTheirs = CheckoutStrategyOpts(GIT_CHECKOUT_USE_THEIRS)

        /** Treat pathspec as simple list of exact match file paths */
        val DisablePathSpecMatch = CheckoutStrategyOpts(GIT_CHECKOUT_DISABLE_PATHSPEC_MATCH)

        /** Ignore directories in use, they will be left empty */
        val SkipLickedDirectories = CheckoutStrategyOpts(GIT_CHECKOUT_SKIP_LOCKED_DIRECTORIES)

        /** Don't overwrite ignored files that exist in the checkout target */
        val DontOverwriteIgnored = CheckoutStrategyOpts(GIT_CHECKOUT_DONT_OVERWRITE_IGNORED)

        /** Write normal merge files for conflicts */
        val ConflictStyleMerge = CheckoutStrategyOpts(GIT_CHECKOUT_CONFLICT_STYLE_MERGE)

        /** Include common ancestor data in diff3 format files for conflicts */
        val ConflictStyleDiff3 = CheckoutStrategyOpts(GIT_CHECKOUT_CONFLICT_STYLE_DIFF3)

        /** Don't overwrite existing files or folders */
        val DontRemoveExisting = CheckoutStrategyOpts(GIT_CHECKOUT_DONT_REMOVE_EXISTING)

        /** Normally checkout writes the index upon completion; this prevents that. */
        val DontWriteIndex = CheckoutStrategyOpts(GIT_CHECKOUT_DONT_WRITE_INDEX)

        /**
         * Show what would be done by a checkout.  Stop after sending
         * notifications; don't update the working directory or index.
         */
        val DryRun = CheckoutStrategyOpts(GIT_CHECKOUT_DRY_RUN)

        /** Include common ancestor data in zdiff3 format for conflicts */
        val ConflictStyleZDiff3 = CheckoutStrategyOpts(GIT_CHECKOUT_CONFLICT_STYLE_ZDIFF3)

        /**
         * THE FOLLOWING OPTIONS ARE NOT YET IMPLEMENTED
         */

        /** Recursively checkout submodules with same options (NOT IMPLEMENTED) */
        val UpdateSubmodules = CheckoutStrategyOpts(GIT_CHECKOUT_UPDATE_SUBMODULES)

        /** Recursively checkout submodules if HEAD moved in super repo (NOT IMPLEMENTED) */
        val UpdateSubmodulesIfChanged = CheckoutStrategyOpts(GIT_CHECKOUT_UPDATE_SUBMODULES_IF_CHANGED)
    }

    override val longValue: ULong
        get() = value.toULong()
}
