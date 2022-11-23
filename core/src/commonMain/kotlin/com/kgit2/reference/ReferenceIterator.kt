package com.kgit2.reference

import com.kgit2.common.error.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.IteratorBase
import com.kgit2.memory.Raw
import kotlinx.cinterop.*
import libgit2.git_reference_iterator
import libgit2.git_reference_iterator_free
import libgit2.git_reference_next

typealias ReferenceIteratorPointer = CPointer<git_reference_iterator>

typealias ReferenceIteratorSecondaryPointer = CPointerVar<git_reference_iterator>

typealias ReferenceIteratorInitial = ReferenceIteratorSecondaryPointer.(Memory) -> Unit

class ReferenceIteratorRaw(
    memory: Memory,
    handler: ReferenceIteratorPointer,
) : Raw<git_reference_iterator>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: ReferenceIteratorSecondaryPointer = memory.allocPointerTo(),
        initial: ReferenceIteratorInitial? = null,
    ) : this(memory, handler.apply {
        runCatching {
            initial?.invoke(handler, memory)
        }.onFailure {
            memory.free()
        }.getOrThrow()
    }.value!!)

    override val beforeFree: () -> Unit = {
        git_reference_iterator_free(handler)
    }
}

class ReferenceIterator(raw: ReferenceIteratorRaw) :
    IteratorBase<git_reference_iterator, ReferenceIteratorRaw, Reference>(raw) {
    constructor(memory: Memory, handler: ReferenceIteratorPointer) : this(ReferenceIteratorRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: ReferenceIteratorSecondaryPointer = memory.allocPointerTo(),
        initial: ReferenceIteratorInitial? = null,
    ) : this(ReferenceIteratorRaw(memory, handler, initial))

    override fun nextRaw(): Result<Reference> = runCatching {
        Reference() {
            git_reference_next(this.ptr, raw.handler).errorCheck()
        }
    }
}
