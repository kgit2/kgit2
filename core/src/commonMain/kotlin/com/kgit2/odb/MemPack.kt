package com.kgit2.odb

import cnames.structs.git_odb
import com.kgit2.common.error.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import com.kgit2.memory.Raw
import com.kgit2.model.toKString
import com.kgit2.model.withGitBuf
import com.kgit2.repository.Repository
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.value
import libgit2.git_mempack_dump
import libgit2.git_mempack_reset
import libgit2.git_odb_backend

typealias MemPackPointer = CPointer<git_odb_backend>

typealias MemPackSecondaryPointer = CPointerVar<git_odb_backend>

typealias MemPackInitial = MemPackSecondaryPointer.(Memory) -> Unit

class MemPackRaw(
    memory: Memory,
    handler: CPointer<git_odb_backend>,
) : Raw<git_odb_backend>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: MemPackSecondaryPointer = memory.allocPointerTo(),
        initial: MemPackInitial? = null,
    ) : this(memory, handler.apply {
        runCatching {
            initial?.invoke(handler, memory)
        }.onFailure {
            memory.free()
        }.getOrThrow()
    }.value!!)
}

class MemPack(raw: MemPackRaw) : GitBase<git_odb_backend, MemPackRaw>(raw) {
    constructor(memory: Memory, handler: CPointer<git_odb_backend>) : this(MemPackRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: MemPackSecondaryPointer = memory.allocPointerTo(),
        initial: MemPackInitial? = null,
    ) : this(MemPackRaw(memory, handler, initial))

    fun dump(repository: Repository) = withGitBuf {
        git_mempack_dump(it, repository.raw.handler, raw.handler).errorCheck()
        it.toKString()
    }

    fun reset() = git_mempack_reset(raw.handler).errorCheck()
}
