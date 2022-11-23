package com.kgit2.branch

import cnames.structs.git_branch_iterator
import com.kgit2.common.error.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.IteratorBase
import com.kgit2.memory.Raw
import kotlinx.cinterop.*
import libgit2.git_branch_iterator_free
import libgit2.git_branch_next
import libgit2.git_branch_tVar

typealias BranchIteratorPointer = CPointer<git_branch_iterator>

typealias BranchIteratorSecondaryPointer = CPointerVar<git_branch_iterator>

typealias BranchIteratorInitial = BranchIteratorSecondaryPointer.(Memory) -> Unit

class BranchIteratorRaw(
    memory: Memory,
    handler: BranchIteratorPointer,
) : Raw<git_branch_iterator>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: BranchIteratorSecondaryPointer = memory.allocPointerTo(),
        initial: BranchIteratorInitial? = null,
    ) : this(memory, handler.apply {
        runCatching {
            initial?.invoke(handler, memory)
        }.onFailure {
            memory.free()
        }.getOrThrow()
    }.value!!)

    override val beforeFree: () -> Unit = {
        git_branch_iterator_free(handler)
    }
}

class BranchIterator(raw: BranchIteratorRaw) : IteratorBase<git_branch_iterator, BranchIteratorRaw, Branch>(raw) {
    constructor(memory: Memory, handler: CPointer<git_branch_iterator>) : this(BranchIteratorRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: BranchIteratorSecondaryPointer = memory.allocPointerTo(),
        initial: BranchIteratorInitial? = null,
    ) : this(BranchIteratorRaw(memory, handler, initial))

    override fun nextRaw(): Result<Branch> = runCatching {
        Branch() { memory ->
            val type = memory.alloc<git_branch_tVar>()
            git_branch_next(this.ptr, type.ptr, raw.handler).errorCheck()
        }
    }
}
