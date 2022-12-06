package com.kgit2.diff

import libgit2.GIT_DIFF_FORMAT_NAME_ONLY
import libgit2.GIT_DIFF_FORMAT_NAME_STATUS
import libgit2.GIT_DIFF_FORMAT_PATCH
import libgit2.GIT_DIFF_FORMAT_PATCH_HEADER
import libgit2.GIT_DIFF_FORMAT_PATCH_ID
import libgit2.GIT_DIFF_FORMAT_RAW
import libgit2.git_diff_format_t

enum class DiffFormat(val value: git_diff_format_t) {
    /**
     * full git diff
     */
    Patch(GIT_DIFF_FORMAT_PATCH),

    /**
     * just the file headers of patch
     */
    PatchHeader(GIT_DIFF_FORMAT_PATCH_HEADER),

    /**
     * like git diff --raw
     */
    Raw(GIT_DIFF_FORMAT_RAW),

    /**
     * like git diff --name-only
     */
    NameOnly(GIT_DIFF_FORMAT_NAME_ONLY),

    /**
     * like git diff --name-status
     */
    NameStatus(GIT_DIFF_FORMAT_NAME_STATUS),

    /**
     * git diff as used by git patch-id
     */
    PatchID(GIT_DIFF_FORMAT_PATCH_ID),
    ;

    companion object {
        fun from(value: git_diff_format_t): DiffFormat {
            return when (value) {
                GIT_DIFF_FORMAT_PATCH -> Patch
                GIT_DIFF_FORMAT_PATCH_HEADER -> PatchHeader
                GIT_DIFF_FORMAT_RAW -> Raw
                GIT_DIFF_FORMAT_NAME_ONLY -> NameOnly
                GIT_DIFF_FORMAT_NAME_STATUS -> NameStatus
                GIT_DIFF_FORMAT_PATCH_ID -> PatchID
                else -> throw IllegalArgumentException("Unknown diff format: $value")
            }
        }
    }
}
