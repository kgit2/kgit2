package com.kgit2.common.option.mutually

import libgit2.*

enum class AutoTagOption(val value: git_remote_autotag_option_t) {
    UnSpecified(GIT_REMOTE_DOWNLOAD_TAGS_UNSPECIFIED),
    Auto(GIT_REMOTE_DOWNLOAD_TAGS_AUTO),
    None(GIT_REMOTE_DOWNLOAD_TAGS_NONE),
    All(GIT_REMOTE_DOWNLOAD_TAGS_ALL);

    companion object {
        fun fromRaw(value: git_remote_autotag_option_t): AutoTagOption {
            return when (value) {
                GIT_REMOTE_DOWNLOAD_TAGS_UNSPECIFIED -> UnSpecified
                GIT_REMOTE_DOWNLOAD_TAGS_AUTO -> Auto
                GIT_REMOTE_DOWNLOAD_TAGS_NONE -> None
                GIT_REMOTE_DOWNLOAD_TAGS_ALL -> All
                else -> error("Unknown value: $value")
            }
        }
    }
}
