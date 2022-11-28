package com.kgit2.status

import com.kgit2.annotations.FlagMask
import libgit2.git_status_show_t
import libgit2.git_status_t

@FlagMask(
    flagsType = git_status_show_t::class,
    "GIT_STATUS_CURRENT",
    "GIT_STATUS_INDEX_NEW",
    "GIT_STATUS_INDEX_MODIFIED",
    "GIT_STATUS_INDEX_DELETED",
    "GIT_STATUS_INDEX_RENAMED",
    "GIT_STATUS_INDEX_TYPECHANGE",
    "GIT_STATUS_WT_NEW",
    "GIT_STATUS_WT_MODIFIED",
    "GIT_STATUS_WT_DELETED",
    "GIT_STATUS_WT_TYPECHANGE",
    "GIT_STATUS_WT_RENAMED",
    "GIT_STATUS_WT_UNREADABLE",
    "GIT_STATUS_IGNORED",
    "GIT_STATUS_CONFLICTED",
    flagsMutable = false
)
class StatusFlag(
    override val flags: git_status_t
) : StatusFlagMask<StatusFlag>
