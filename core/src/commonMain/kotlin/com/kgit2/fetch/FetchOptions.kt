package com.kgit2.fetch

import com.kgit2.proxy.ProxyOptions
import com.kgit2.common.option.mutually.AutoTagOption
import com.kgit2.model.AutoFreeGitBase
import com.kgit2.remote.RemoteRedirect
import com.kgit2.model.toList
import com.kgit2.remote.RemoteCallbacks
import kotlinx.cinterop.*
import libgit2.GIT_FETCH_OPTIONS_VERSION
import libgit2.git_fetch_options
import libgit2.git_fetch_options_init

class FetchOptions(
    override val handler: CPointer<git_fetch_options>,
    override val arena: Arena,
) : AutoFreeGitBase<CPointer<git_fetch_options>> {
    companion object {
        fun initialized(): FetchOptions {
            val arena = Arena()
            val handler = arena.alloc<git_fetch_options>()
            git_fetch_options_init(handler.ptr, GIT_FETCH_OPTIONS_VERSION)
            return FetchOptions(handler.ptr, arena)
        }
    }

    val callbacks: RemoteCallbacks = RemoteCallbacks(handler.pointed.callbacks.ptr, arena)

    val proxy: ProxyOptions = ProxyOptions(handler.pointed.proxy_opts.ptr, arena)

    var prune: FetchPrune = FetchPrune.fromRaw(handler.pointed.prune)
        set(value) {
            field = value
            handler.pointed.prune = value.value
        }

    var updateFetchHead = handler.pointed.update_fetchhead
        set(value) {
            field = value
            handler.pointed.update_fetchhead = value
        }

    var downloadTags: AutoTagOption = AutoTagOption.fromRaw(handler.pointed.download_tags)
        set(value) {
            field = value
            handler.pointed.download_tags = value.value
        }

    var followRedirects: RemoteRedirect = RemoteRedirect.fromRaw(handler.pointed.follow_redirects)
        set(value) {
            field = value
            handler.pointed.follow_redirects = value.value
        }

    private val customHeaders: MutableList<String> = handler.pointed.custom_headers.ptr.toList().toMutableList()

    fun customHeaders(): List<String> = customHeaders.toList()

    fun customHeaders(process: MutableList<String>.() -> Unit) {
        process(customHeaders)
        memScoped {
            handler.pointed.custom_headers.strings = customHeaders.toCStringArray(this)
        }
        handler.pointed.custom_headers.count = customHeaders.size.convert()
    }
}

