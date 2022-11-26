package com.kgit2.index

import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.ptr
import libgit2.*

@Raw(
    base = git_index::class,
    free = "git_index_free",
)
class Index(raw: IndexRaw) : GitBase<git_index, IndexRaw>(raw) {
    constructor(memory: Memory, handler: IndexPointer) : this(IndexRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        secondary: IndexSecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: IndexSecondaryInitial = {
            git_index_new(this.ptr)
        }
    ) : this(IndexRaw(memory, secondary, secondaryInitial))

    constructor(path: String) : this(secondaryInitial = {
        git_index_open(this.ptr, path)
    })

    var version = git_index_version(raw.handler)
        set(value) {
            field = value
            git_index_set_version(raw.handler, value)
        }



}
