package com.kgit2.status

import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.diff.DiffDelta
import com.kgit2.memory.GitBase
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.pointed
import libgit2.git_status_entry

@Raw(
    base = "git_status_entry"
)
class StatusEntry(raw: StatusEntryRaw) : GitBase<git_status_entry, StatusEntryRaw>(raw) {
    constructor(memory: Memory, handler: StatusEntryPointer) : this(StatusEntryRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: StatusEntrySecondaryPointer = memory.allocPointerTo(),
        initial: StatusEntryInitial? = null,
    ) : this(StatusEntryRaw(memory, handler, initial))

    val headToIndex: DiffDelta? = raw.handler.pointed.head_to_index?.let {
        DiffDelta(Memory(), it)
    }

    val indexToWorkDir: DiffDelta? = raw.handler.pointed.index_to_workdir?.let {
        DiffDelta(Memory(), it)
    }

    val path: String? = headToIndex?.path ?: indexToWorkDir?.path

    /**
     * note: There have multiple status for a StatusEntry
     */
    val status: Status = Status(raw.handler.pointed.status)

    val isCurrent: Boolean = Status.CURRENT in status

    val isIndexNew: Boolean = Status.INDEX_NEW in status

    val isIndexModified: Boolean = Status.INDEX_MODIFIED in status

    val isIndexDeleted: Boolean = Status.INDEX_DELETED in status

    val isIndexRenamed: Boolean = Status.INDEX_RENAMED in status

    val isIndexTypeChange: Boolean = Status.INDEX_TYPECHANGE in status

    val isWorkTreeNew: Boolean = Status.WT_NEW in status

    val isWorkTreeModified: Boolean = Status.WT_MODIFIED in status

    val isWorkTreeDeleted: Boolean = Status.WT_DELETED in status

    val isWorkTreeTypeChange: Boolean = Status.WT_TYPECHANGE in status

    val isWorkTreeRenamed: Boolean = Status.WT_RENAMED in status

    val isWorkTreeUnreadable: Boolean = Status.WT_UNREADABLE in status

    val isIgnored: Boolean = Status.IGNORED in status

    val isConflicted: Boolean = Status.CONFLICTED in status
}
