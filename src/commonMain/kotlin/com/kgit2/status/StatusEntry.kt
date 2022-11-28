package com.kgit2.status

import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.diff.DiffDelta
import com.kgit2.memory.GitBase
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.pointed
import libgit2.git_status_entry

@Raw(
    base = git_status_entry::class,
)
class StatusEntry(raw: StatusEntryRaw) : GitBase<git_status_entry, StatusEntryRaw>(raw) {
    constructor(memory: Memory, handler: StatusEntryPointer) : this(StatusEntryRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        secondary: StatusEntrySecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: StatusEntrySecondaryInitial? = null,
    ) : this(StatusEntryRaw(memory, secondary, secondaryInitial))

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
    val status: StatusFlag = StatusFlag(raw.handler.pointed.status)
}
