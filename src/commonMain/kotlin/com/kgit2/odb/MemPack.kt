package com.kgit2.odb

import com.kgit2.annotations.Raw
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.RawWrapper
import com.kgit2.model.Buf
import com.kgit2.repository.Repository
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.allocPointerTo
import libgit2.git_mempack_dump
import libgit2.git_mempack_reset
import libgit2.git_odb_backend

@Raw(
    base = git_odb_backend::class,
)
class MemPack(raw: OdbBackendRaw) : RawWrapper<git_odb_backend, OdbBackendRaw>(raw) {
    constructor(memory: Memory, handler: CPointer<git_odb_backend>) : this(OdbBackendRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        secondary: OdbBackendSecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: OdbBackendSecondaryInitial? = null,
    ) : this(OdbBackendRaw(memory, secondary, secondaryInitial))

    fun dump(repository: Repository): Buf = Buf {
        git_mempack_dump(this, repository.raw.handler, raw.handler).errorCheck()
    }

    fun reset() = git_mempack_reset(raw.handler).errorCheck()
}
