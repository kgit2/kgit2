package com.kgit2.status

import cnames.structs.git_status_list
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import com.kgit2.memory.Raw
import kotlinx.atomicfu.AtomicInt
import kotlinx.atomicfu.atomic
import kotlinx.cinterop.*
import libgit2.git_status_byindex
import libgit2.git_status_list_entrycount
import libgit2.git_status_list_free

typealias StatusListPointer = CPointer<git_status_list>

typealias StatusListSecondaryPointer = CPointerVar<git_status_list>

typealias StatusListInitial = StatusListSecondaryPointer.(Memory) -> Unit

class StatusListRaw(
    memory: Memory,
    handler: StatusListPointer,
) : Raw<git_status_list>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: StatusListSecondaryPointer = memory.allocPointerTo(),
        initial: StatusListInitial? = null,
    ) : this(memory, handler.apply {
        runCatching {
            initial?.invoke(handler, memory)
        }.onFailure {
            git_status_list_free(handler.value!!)
            memory.free()
        }.getOrThrow()
    }.value!!)

    override val beforeFree: () -> Unit = {
        git_status_list_free(handler)
    }
}

class StatusList(raw: StatusListRaw) : GitBase<git_status_list, StatusListRaw>(raw), Iterable<StatusEntry> {
    constructor(memory: Memory, handler: StatusListPointer) : this(StatusListRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: StatusListSecondaryPointer = memory.allocPointerTo(),
        initial: StatusListInitial? = null
    ) : this(StatusListRaw(memory, handler, initial))

    operator fun get(index: Int): StatusEntry {
        git_status_byindex(raw.handler, index.convert())?.let {
            return StatusEntry(Memory(), it)
        } ?: throw IndexOutOfBoundsException()
    }

    val count: Int = git_status_list_entrycount(raw.handler).convert()

    /**
     * Returns an iterator over the elements of this object.
     */
    override fun iterator(): Iterator<StatusEntry> = StatusIterator()

    inner class StatusIterator : Iterator<StatusEntry> {
        private val currentIndex: AtomicInt = atomic(0)

        /**
         * Returns `true` if the iteration has more elements.
         */
        override fun hasNext(): Boolean = currentIndex.value < count

        /**
         * Returns the next element in the iteration.
         */
        override fun next(): StatusEntry {
            val index = currentIndex.incrementAndGet()
            return get(index)
        }
    }
}