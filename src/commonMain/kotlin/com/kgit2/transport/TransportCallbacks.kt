package com.kgit2.transport

import cnames.structs.git_remote
import com.kgit2.common.callback.CallbackResult
import com.kgit2.common.memory.Memory
import com.kgit2.remote.Remote
import kotlinx.cinterop.*
import libgit2.git_transport
import libgit2.git_transport_cb

/**
 * Callback for the user's custom transport.
 *
 * @param transport The transport to be used
 * @param remote The remote
 * @return 0 to proceed with the push, < 0 to fail the push
 */
typealias TransportCallback = (transport: Transport, remote: Remote) -> CallbackResult

interface TransportCallbackPayload {
    var transportCallback: TransportCallback?
}

val staticTransportCallback: git_transport_cb = staticCFunction {
        transport: CPointer<CPointerVar<git_transport>>?,
        remote: CPointer<git_remote>?,
        payload: COpaquePointer?,
    ->
    val callback = payload!!.asStableRef<TransportCallbackPayload>().get()
    callback.transportCallback!!.invoke(
        Transport(Memory(), transport!!.pointed.value!!),
        Remote(Memory(), remote!!)
    ).value
}
