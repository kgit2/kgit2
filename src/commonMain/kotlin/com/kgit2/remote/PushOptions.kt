package com.kgit2.remote

import com.kgit2.model.AutoFreeGitBase
import com.kgit2.proxy.ProxyOptions
import kotlinx.cinterop.*
import libgit2.GIT_PUSH_OPTIONS_VERSION
import libgit2.git_push_options
import libgit2.git_push_options_init

class PushOptions(
    override val handler: CPointer<git_push_options>,
    override val arena: Arena,
) : AutoFreeGitBase<CPointer<git_push_options>> {
    companion object {
        fun initialized(): PushOptions {
            val arena = Arena()
            val handler = arena.alloc<git_push_options>()
            git_push_options_init(handler.ptr, GIT_PUSH_OPTIONS_VERSION)
            return PushOptions(handler.ptr, arena)
        }
    }

    /**
     * Callbacks to use for this push operation
     */
    val callbacks: RemoteCallbacks = RemoteCallbacks(handler.pointed.callbacks.ptr, arena)

    /**
     * Proxy options to use for this push operation
     */
    val proxy: ProxyOptions = ProxyOptions(handler.pointed.proxy_opts.ptr, arena)

    /**
     * If the transport being used to push to the remote requires the creation
     * of a pack file, this controls the number of worker threads used by
     * the packbuilder when creating that pack file to be sent to the remote.
     *
     * If set to 0, the packbuilder will auto-detect the number of threads
     * to create. The default value is 1.
     */
    var pbParallelism: UInt = handler.pointed.pb_parallelism
        set(value) {
            field = value
            handler.pointed.pb_parallelism = value
        }

    var followRedirects: RemoteRedirect = RemoteRedirect.fromRaw(handler.pointed.follow_redirects)
        set(value) {
            field = value
            handler.pointed.follow_redirects = value.value
        }
}
