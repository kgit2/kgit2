package com.kgit2.branch

import cnames.structs.git_branch_iterator
import com.kgit2.annotations.Raw
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.IterableBase
import com.kgit2.memory.IteratorBase
import com.kgit2.memory.RawWrapper
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.ptr
import libgit2.git_branch_next
import libgit2.git_branch_tVar

@Raw(
    base = git_branch_iterator::class,
    free = "git_branch_iterator_free",
)
class BranchIterator(raw: BranchIteratorRaw) : RawWrapper<git_branch_iterator, BranchIteratorRaw>(raw), Iterable<Branch> {
    constructor(memory: Memory, handler: CPointer<git_branch_iterator>) : this(BranchIteratorRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        secondary: BranchIteratorSecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: BranchIteratorSecondaryInitial? = null,
    ) : this(BranchIteratorRaw(memory, secondary, secondaryInitial))

    inner class InnerIterator : IteratorBase<Branch>() {
        override fun nextRaw(): Result<Branch> = runCatching {
            Branch {
                val type = it.alloc<git_branch_tVar>()
                git_branch_next(this.ptr, type.ptr, raw.handler).errorCheck()
            }
        }
    }

    override fun iterator(): Iterator<Branch> = InnerIterator()
}
