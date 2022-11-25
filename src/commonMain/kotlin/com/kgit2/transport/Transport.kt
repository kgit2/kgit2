package com.kgit2.transport

import cnames.structs.git_transport
import com.kgit2.common.memory.Memory
import com.kgit2.memory.Raw
import com.kgit2.memory.GitBase
import com.kgit2.remote.Remote
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.value

typealias TransportPointer = CPointer<git_transport>

typealias TransportSecondaryPointer = CPointerVar<git_transport>

typealias TransportInitial = TransportSecondaryPointer.(Memory) -> Unit

class TransportRaw(
    memory: Memory,
    handler: TransportPointer,
) : Raw<git_transport>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: TransportSecondaryPointer = memory.allocPointerTo(),
        initial: TransportInitial? = null,
    ) : this(memory, handler.apply {
        runCatching {
            initial?.invoke(handler, memory)
        }.onFailure {
            memory.free()
        }.getOrThrow()
    }.value!!)
}

class Transport(raw: TransportRaw) : GitBase<git_transport, TransportRaw>(raw) {
    constructor(memory: Memory, handler: TransportPointer) : this(TransportRaw(memory, handler))

    fun <T : SmartSubTransport> smart(remote: Remote, rpc: Boolean, subTransport: T): Transport {
        TODO("Not yet implemented")
    }
}

interface SmartSubTransport {
    fun action(url: String, action: TransportService): Sequence<Byte>

    fun free()
}
