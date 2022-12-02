package com.kgit2.submodule

import com.kgit2.annotations.FlagMask
import libgit2.git_submodule_status_t

@FlagMask(
    flagsType = git_submodule_status_t::class,
    "GIT_SUBMODULE_STATUS_IN_HEAD",
    "GIT_SUBMODULE_STATUS_IN_INDEX",
    "GIT_SUBMODULE_STATUS_IN_CONFIG",
    "GIT_SUBMODULE_STATUS_IN_WD",
    "GIT_SUBMODULE_STATUS_INDEX_ADDED",
    "GIT_SUBMODULE_STATUS_INDEX_DELETED",
    "GIT_SUBMODULE_STATUS_INDEX_MODIFIED",
    "GIT_SUBMODULE_STATUS_WD_UNINITIALIZED",
    "GIT_SUBMODULE_STATUS_WD_ADDED",
    "GIT_SUBMODULE_STATUS_WD_DELETED",
    "GIT_SUBMODULE_STATUS_WD_MODIFIED",
    "GIT_SUBMODULE_STATUS_WD_INDEX_MODIFIED",
    "GIT_SUBMODULE_STATUS_WD_WD_MODIFIED",
    "GIT_SUBMODULE_STATUS_WD_UNTRACKED",
    flagsMutable = false
)
data class SubmoduleStatus(
    override var flags: git_submodule_status_t,
) : SubmoduleStatusMask<SubmoduleStatus>
