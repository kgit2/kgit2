package com.kgit2.fetch

import libgit2.git_fetch_prune_t

enum class FetchPrune(val value: git_fetch_prune_t) {
    UnSpecified(git_fetch_prune_t.GIT_FETCH_PRUNE_UNSPECIFIED),
    NoPrune(git_fetch_prune_t.GIT_FETCH_NO_PRUNE),
    OffPrune(git_fetch_prune_t.GIT_FETCH_PRUNE);

    companion object {
        fun fromRaw(value: git_fetch_prune_t): FetchPrune {
            return when (value) {
                git_fetch_prune_t.GIT_FETCH_PRUNE_UNSPECIFIED -> UnSpecified
                git_fetch_prune_t.GIT_FETCH_NO_PRUNE -> NoPrune
                git_fetch_prune_t.GIT_FETCH_PRUNE -> OffPrune
            }
        }
    }
}
