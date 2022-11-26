package com.kgit2.index

import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import libgit2.git_index_entry

@Raw(
    base = git_index_entry::class,
)
class IndexEntry(raw: IndexEntryRaw = IndexEntryRaw()) : GitBase<git_index_entry, IndexEntryRaw>(raw) {
    constructor(memory: Memory, handler: IndexEntryPointer) : this(IndexEntryRaw(memory, handler))
}
