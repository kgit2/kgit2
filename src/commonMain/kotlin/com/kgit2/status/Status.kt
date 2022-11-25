package com.kgit2.status

import com.kgit2.common.option.BaseMultiple
import libgit2.*

class Status(val value: git_status_t) : BaseMultiple<Status>() {
    companion object {
        val CURRENT = Status(GIT_STATUS_CURRENT)
        val INDEX_NEW = Status(GIT_STATUS_INDEX_NEW)
        val INDEX_MODIFIED = Status(GIT_STATUS_INDEX_MODIFIED)
        val INDEX_DELETED = Status(GIT_STATUS_INDEX_DELETED)
        val INDEX_RENAMED = Status(GIT_STATUS_INDEX_RENAMED)
        val INDEX_TYPECHANGE = Status(GIT_STATUS_INDEX_TYPECHANGE)
        val WT_NEW = Status(GIT_STATUS_WT_NEW)
        val WT_MODIFIED = Status(GIT_STATUS_WT_MODIFIED)
        val WT_DELETED = Status(GIT_STATUS_WT_DELETED)
        val WT_TYPECHANGE = Status(GIT_STATUS_WT_TYPECHANGE)
        val WT_RENAMED = Status(GIT_STATUS_WT_RENAMED)
        val WT_UNREADABLE = Status(GIT_STATUS_WT_UNREADABLE)
        val IGNORED = Status(GIT_STATUS_IGNORED)
        val CONFLICTED = Status(GIT_STATUS_CONFLICTED)
    }

    override val longValue: ULong
        get() = value.toULong()
}
