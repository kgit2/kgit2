package com.kgit2.apply

import libgit2.GIT_APPLY_LOCATION_BOTH
import libgit2.GIT_APPLY_LOCATION_INDEX
import libgit2.GIT_APPLY_LOCATION_WORKDIR
import libgit2.git_apply_location_t

enum class ApplyLocation(val value: git_apply_location_t) {
    /**
     * Apply the patch to the workdir, leaving the index untouched. This is the equivalent of git apply with no location argument.
     */
    WorkDir(GIT_APPLY_LOCATION_WORKDIR),

    /**
     * Apply the patch to the index, leaving the working directory untouched. This is the equivalent of git apply --cached.
     */
    Index(GIT_APPLY_LOCATION_INDEX),

    /**
     * Apply the patch to both the working directory and the index. This is the equivalent of git apply --index
     */
    Both(GIT_APPLY_LOCATION_BOTH),
    ;

    companion object {
        fun from(value: git_apply_location_t): ApplyLocation {
            return when (value) {
                GIT_APPLY_LOCATION_WORKDIR -> WorkDir
                GIT_APPLY_LOCATION_INDEX -> Index
                GIT_APPLY_LOCATION_BOTH -> Both
                else -> throw IllegalArgumentException("Unknown git_apply_location_t value: $value")
            }
        }
    }
}
