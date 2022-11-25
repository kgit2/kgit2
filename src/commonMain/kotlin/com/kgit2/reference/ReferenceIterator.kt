package com.kgit2.reference

import com.kgit2.annotations.Raw
import com.kgit2.common.error.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.IteratorBase
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.ptr
import libgit2.git_reference_iterator
import libgit2.git_reference_next

@Raw(
    base = "git_reference_iterator",
    free = "git_reference_iterator_free"
)
class ReferenceIterator(raw: ReferenceIteratorRaw) :
    IteratorBase<git_reference_iterator, ReferenceIteratorRaw, Reference>(raw) {
    constructor(memory: Memory, handler: ReferenceIteratorPointer) : this(ReferenceIteratorRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: ReferenceIteratorSecondaryPointer = memory.allocPointerTo(),
        initial: ReferenceIteratorInitial? = null,
    ) : this(ReferenceIteratorRaw(memory, handler, initial))

    override fun nextRaw(): Result<Reference> = runCatching {
        Reference {
            git_reference_next(this.ptr, raw.handler).errorCheck()
        }
    }
}
