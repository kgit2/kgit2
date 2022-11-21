package com.kgit2.remote

import com.kgit2.common.error.toBoolean
import com.kgit2.common.memory.Memory
import com.kgit2.memory.Binding
import com.kgit2.memory.GitBase
import com.kgit2.model.Oid
import kotlinx.cinterop.*
import libgit2.git_remote_head

typealias RemoteHeadPointer = CPointer<git_remote_head>

typealias RemoteHeadSecondaryPointer = CPointerVar<git_remote_head>

typealias RemoteHeadInitial = RemoteHeadSecondaryPointer.(Memory) -> Unit

class RemoteHeadRaw(
    memory: Memory,
    handler: RemoteHeadPointer,
) : Binding<git_remote_head>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: RemoteHeadSecondaryPointer = memory.allocPointerTo(),
        init: RemoteHeadInitial? = null,
    ) : this(memory, handler.apply {
        runCatching {
            init?.invoke(handler, memory)
        }.onFailure {
            memory.free()
        }.getOrThrow()
    }.value!!)
}

class RemoteHead(raw: RemoteHeadRaw) : GitBase<git_remote_head, RemoteHeadRaw>(raw) {
    constructor(memory: Memory, handler: RemoteHeadPointer) : this(RemoteHeadRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: RemoteHeadSecondaryPointer = memory.allocPointerTo(),
        init: RemoteHeadInitial? = null,
    ) : this(RemoteHeadRaw(memory, handler, init))

    val isLocal: Boolean = raw.handler.pointed.local.toBoolean()

    val name: String = raw.handler.pointed.name!!.toKString()

    val oid: Oid = Oid(Memory(), raw.handler.pointed.oid.ptr)

    val local: Oid = Oid(Memory(), raw.handler.pointed.loid.ptr)

    val symrefTarget: String? = raw.handler.pointed.symref_target?.toKString()
}
