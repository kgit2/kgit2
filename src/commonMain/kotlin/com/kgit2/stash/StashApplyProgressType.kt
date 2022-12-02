package com.kgit2.stash

import libgit2.*

enum class StashApplyProgressType(val value: git_stash_apply_progress_t) {
    /**
     *
     */
    ApplyProgressNone(GIT_STASH_APPLY_PROGRESS_NONE),

    /**
     * Loading the stashed data from the object database.
     */
    ApplyProgressLoadingStash(GIT_STASH_APPLY_PROGRESS_LOADING_STASH),

    /**
     * The stored index is being analyzed.
     */
    ApplyProgressAnalyzeIndex(GIT_STASH_APPLY_PROGRESS_ANALYZE_INDEX),

    /**
     * The modified files are being analyzed.
     */
    ApplyProgressAnalyzeModified(GIT_STASH_APPLY_PROGRESS_ANALYZE_MODIFIED),

    /**
     * The untracked and ignored files are being analyzed.
     */
    ApplyProgressAnalyzeUntracked(GIT_STASH_APPLY_PROGRESS_ANALYZE_UNTRACKED),

    /**
     * The untracked files are being written to disk.
     */
    ApplyProgressCheckoutUntracked(GIT_STASH_APPLY_PROGRESS_CHECKOUT_UNTRACKED),

    /**
     * The modified files are being written to disk.
     */
    ApplyProgressCheckoutModified(GIT_STASH_APPLY_PROGRESS_CHECKOUT_MODIFIED),

    /**
     * The stash was applied successfully.
     */
    ApplyProgressDone(GIT_STASH_APPLY_PROGRESS_DONE),
    ;

    companion object {
        fun from(value: git_stash_apply_progress_t): StashApplyProgressType {
            return when (value) {
                GIT_STASH_APPLY_PROGRESS_NONE -> ApplyProgressNone
                GIT_STASH_APPLY_PROGRESS_LOADING_STASH -> ApplyProgressLoadingStash
                GIT_STASH_APPLY_PROGRESS_ANALYZE_INDEX -> ApplyProgressAnalyzeIndex
                GIT_STASH_APPLY_PROGRESS_ANALYZE_MODIFIED -> ApplyProgressAnalyzeModified
                GIT_STASH_APPLY_PROGRESS_ANALYZE_UNTRACKED -> ApplyProgressAnalyzeUntracked
                GIT_STASH_APPLY_PROGRESS_CHECKOUT_UNTRACKED -> ApplyProgressCheckoutUntracked
                GIT_STASH_APPLY_PROGRESS_CHECKOUT_MODIFIED -> ApplyProgressCheckoutModified
                GIT_STASH_APPLY_PROGRESS_DONE -> ApplyProgressDone
                else -> throw IllegalArgumentException("Unknown StashApplyProgressType value: $value")
            }
        }
    }
}
