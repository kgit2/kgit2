package com.kgit2.odb

import cnames.structs.git_odb_backend
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import com.kgit2.memory.Raw
import kotlinx.cinterop.*

typealias OdbBackendPointer = CPointer<git_odb_backend>

typealias OdbBackendSecondaryPointer = CPointerVar<git_odb_backend>

typealias OdbBackendInitial = OdbBackendSecondaryPointer.(Memory) -> Unit

class OdbBackendRaw(
    memory: Memory,
    handler: OdbBackendPointer,
) : Raw<git_odb_backend>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: OdbBackendSecondaryPointer = memory.allocPointerTo(),
        initial: OdbBackendInitial? = null,
    ) : this(memory, handler.apply {
        runCatching {
            initial?.invoke(handler, memory)
        }.onFailure {
            memory.free()
        }.getOrThrow()
    }.value!!)
}

class OdbBackend(raw: OdbBackendRaw) : GitBase<git_odb_backend, OdbBackendRaw>(raw) {
    constructor(memory: Memory, handler: OdbBackendPointer) : this(OdbBackendRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: OdbBackendSecondaryPointer = memory.allocPointerTo(),
        initial: OdbBackendInitial? = null,
    ) : this(OdbBackendRaw(memory, handler, initial))

    // val a = raw.handler.pointed
    // val odb: Odb = Odb(Memory(), )
}
