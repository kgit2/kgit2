package com.kgit2.transport

import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import com.kgit2.remote.Remote
import libgit2.git_transport

@Raw(
    base = git_transport::class,
)
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
