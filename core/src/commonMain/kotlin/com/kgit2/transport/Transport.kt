package com.kgit2.transport

import cnames.structs.git_transport
import com.kgit2.model.AutoFreeGitBase
import com.kgit2.remote.Remote
import kotlinx.cinterop.Arena
import kotlinx.cinterop.CPointer

class Transport(
    override val handler: CPointer<git_transport>,
    override val arena: Arena,
) : AutoFreeGitBase<CPointer<git_transport>> {

    fun <T : SmartSubTransport> smart(remote: Remote, rpc: Boolean, subTransport: T): Transport {
        TODO("Not yet implemented")
    }
}

interface SmartSubTransport {
    fun action(url: String, action: TransportService): Sequence<Byte>

    fun free()
}
