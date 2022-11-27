package com.kgit2.index

import cnames.structs.git_index_conflict_iterator
import com.kgit2.annotations.Raw
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.IteratorBase
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.ptr
import libgit2.git_index_conflict_next

@Raw(
    base = git_index_conflict_iterator::class,
    free = "git_index_conflict_iterator_free",
)
class IndexConflictIterator(raw: IndexConflictIteratorRaw) :
    IteratorBase<
        git_index_conflict_iterator,
        IndexConflictIteratorRaw,
        Triple<IndexEntry, IndexEntry, IndexEntry>
    >(raw) {
    constructor(
        memory: Memory = Memory(),
        secondary: IndexConflictIteratorSecondaryPointer = memory.allocPointerTo(),
        initial: IndexConflictIteratorSecondaryInitial? = null,
    ) : this(IndexConflictIteratorRaw(memory, secondary, initial))

    override fun nextRaw(): Result<Triple<IndexEntry, IndexEntry, IndexEntry>> = runCatching {
        lateinit var ancestor: IndexEntry
        lateinit var ours: IndexEntry
        lateinit var theirs: IndexEntry
        ancestor = IndexEntry(secondaryInitial = ancestor@ {
            ours = IndexEntry(secondaryInitial = ours@ {
                theirs = IndexEntry(secondaryInitial = theirs@ {
                    git_index_conflict_next((this@ancestor).ptr, (this@ours).ptr, (this@theirs).ptr, raw.handler).errorCheck()
                })
            })
        })
        Triple(ancestor, ours, theirs)
    }
}
