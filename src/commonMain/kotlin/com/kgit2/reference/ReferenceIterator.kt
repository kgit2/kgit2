package com.kgit2.reference

import com.kgit2.annotations.Raw
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.IteratorBase
import com.kgit2.memory.RawWrapper
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.ptr
import libgit2.git_reference_iterator
import libgit2.git_reference_next
import kotlin.native.ref.WeakReference

@Raw(
    base = git_reference_iterator::class,
    free = "git_reference_iterator_free"
)
class ReferenceIterator(raw: ReferenceIteratorRaw)
    : RawWrapper<git_reference_iterator, ReferenceIteratorRaw>(raw),
    IteratorBase<Reference> {
    constructor(
        memory: Memory = Memory(),
        secondary: ReferenceIteratorSecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: ReferenceIteratorSecondaryInitial? = null,
    ) : this(ReferenceIteratorRaw(memory, secondary, secondaryInitial))

    override var next: WeakReference<Reference>? = null

    override fun nextRaw(): Result<Reference> = runCatching {
        Reference {
            git_reference_next(this.ptr, raw.handler).errorCheck()
        }
    }
}
