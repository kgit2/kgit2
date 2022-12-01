package com.kgit2.status

import cnames.structs.git_status_list
import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.memory.IterableBase
import com.kgit2.memory.RawWrapper
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.convert
import libgit2.git_status_byindex
import libgit2.git_status_list_entrycount

@Raw(
    base = git_status_list::class,
    free = "git_status_list_free",
)
class StatusList(raw: StatusListRaw) : RawWrapper<git_status_list, StatusListRaw>(raw), IterableBase<StatusEntry> {
    constructor(memory: Memory, handler: StatusListPointer) : this(StatusListRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        secondary: StatusListSecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: StatusListSecondaryInitial? = null,
    ) : this(StatusListRaw(memory, secondary, secondaryInitial))

    val count: Int = git_status_list_entrycount(raw.handler).convert()

    override val size: Long = count.toLong()

    override operator fun get(index: Long): StatusEntry = git_status_byindex(raw.handler, index.convert())?.let {
        return StatusEntry(Memory(), it)
    } ?: throw IndexOutOfBoundsException()
}
