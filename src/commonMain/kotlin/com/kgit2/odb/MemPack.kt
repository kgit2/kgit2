package com.kgit2.odb

import com.kgit2.annotations.Raw
import com.kgit2.common.error.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import com.kgit2.model.toKString
import com.kgit2.model.withGitBuf
import com.kgit2.repository.Repository
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.allocPointerTo
import libgit2.git_mempack_dump
import libgit2.git_mempack_reset
import libgit2.git_odb_backend

@Raw(
    base = "git_odb_backend",
)
class MemPack(raw: OdbBackendRaw) : GitBase<git_odb_backend, OdbBackendRaw>(raw) {
    constructor(memory: Memory, handler: CPointer<git_odb_backend>) : this(OdbBackendRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: OdbBackendSecondaryPointer = memory.allocPointerTo(),
        initial: OdbBackendInitial? = null,
    ) : this(OdbBackendRaw(memory, handler, initial))

    fun dump(repository: Repository) = withGitBuf {
        git_mempack_dump(it, repository.raw.handler, raw.handler).errorCheck()
        it.toKString()
    }

    fun reset() = git_mempack_reset(raw.handler).errorCheck()
}
