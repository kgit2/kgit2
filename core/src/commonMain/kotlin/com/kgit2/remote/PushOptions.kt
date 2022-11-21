package com.kgit2.remote

import com.kgit2.common.memory.Memory
import com.kgit2.memory.Binding
import com.kgit2.memory.GitBase
import com.kgit2.proxy.ProxyOptions
import kotlinx.cinterop.*
import libgit2.GIT_PUSH_OPTIONS_VERSION
import libgit2.git_push_options
import libgit2.git_push_options_init

typealias PushOptionsPointer = CPointer<git_push_options>

typealias PushOptionsSecondaryPointer = CPointerVar<git_push_options>

typealias PushOptionsInitial = PushOptionsSecondaryPointer.(Memory) -> Unit

class PushOptionsRaw(
    memory: Memory = Memory(),
    handler: PushOptionsPointer = memory.alloc<git_push_options>().ptr,
) : Binding<git_push_options>(memory, handler) {
    init {
        runCatching {
            git_push_options_init(handler, GIT_PUSH_OPTIONS_VERSION)
        }.onFailure {
            memory.free()
        }.getOrThrow()
    }
}

class PushOptions(
    raw: PushOptionsRaw = PushOptionsRaw(),
) : GitBase<git_push_options, PushOptionsRaw>(raw) {

    /**
     * Callbacks to use for this push operation
     */
    val callbacks: RemoteCallbacks = RemoteCallbacks(Memory(), raw.handler.pointed.callbacks.ptr)

    /**
     * Proxy options to use for this push operation
     */
    val proxy: ProxyOptions = ProxyOptions(Memory(), raw.handler.pointed.proxy_opts.ptr)

    /**
     * If the transport being used to push to the remote requires the creation
     * of a pack file, this controls the number of worker threads used by
     * the packbuilder when creating that pack file to be sent to the remote.
     *
     * If set to 0, the packbuilder will auto-detect the number of threads
     * to create. The default value is 1.
     */
    var pbParallelism: UInt = raw.handler.pointed.pb_parallelism
        set(value) {
            field = value
            raw.handler.pointed.pb_parallelism = value
        }

    var followRedirects: RemoteRedirect = RemoteRedirect.fromRaw(raw.handler.pointed.follow_redirects)
        set(value) {
            field = value
            raw.handler.pointed.follow_redirects = value.value
        }
}
