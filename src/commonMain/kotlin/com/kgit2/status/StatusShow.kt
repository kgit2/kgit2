package com.kgit2.status

import libgit2.*

enum class StatusShow(val value: git_status_show_t) {
    IndexAndWorkDir(GIT_STATUS_SHOW_INDEX_AND_WORKDIR),
    IndexOnly(GIT_STATUS_SHOW_INDEX_ONLY),
    WorkDirOnly(GIT_STATUS_SHOW_WORKDIR_ONLY),
    ;

    companion object {
        fun fromRaw(value: git_status_show_t): StatusShow {
            return when (value) {
                GIT_STATUS_SHOW_INDEX_AND_WORKDIR -> IndexAndWorkDir
                GIT_STATUS_SHOW_INDEX_ONLY -> IndexOnly
                GIT_STATUS_SHOW_WORKDIR_ONLY -> WorkDirOnly
                else -> throw IllegalArgumentException("Unknown status show type: $value")
            }
        }
    }
}
