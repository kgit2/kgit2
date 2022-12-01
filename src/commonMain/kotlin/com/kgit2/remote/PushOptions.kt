package com.kgit2.remote

import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.memory.RawWrapper
import com.kgit2.proxy.ProxyOptions
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import libgit2.git_push_options

@Raw(
    base = git_push_options::class,
)
class PushOptions(
    raw: PushOptionsRaw = PushOptionsRaw(),
) : RawWrapper<git_push_options, PushOptionsRaw>(raw) {

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
