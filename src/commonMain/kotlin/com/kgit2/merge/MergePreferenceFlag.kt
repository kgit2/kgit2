package com.kgit2.merge

import com.kgit2.annotations.FlagMask
import libgit2.git_merge_preference_t

@FlagMask(
    flagsType = git_merge_preference_t::class,
    /**
     * No configuration was found that suggests a preferred behavior for merge.
     */
    "GIT_MERGE_PREFERENCE_NONE",
    /**
     * There is a merge.ff=false configuration setting, suggesting that the user does not want to allow a fast-forward merge.
     */
    "GIT_MERGE_PREFERENCE_NO_FASTFORWARD",
    /**
     * There is a merge.ff=only configuration setting, suggesting that the user only wants fast-forward merges.
     */
    "GIT_MERGE_PREFERENCE_FASTFORWARD_ONLY",
    flagsMutable = false,
)
data class MergePreferenceFlag(
    override val flags: git_merge_preference_t,
) : MergePreferenceFlagMask<MergePreferenceFlag>
