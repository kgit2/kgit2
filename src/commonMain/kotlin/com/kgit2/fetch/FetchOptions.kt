package com.kgit2.fetch

import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.common.option.mutually.AutoTagOption
import com.kgit2.memory.GitBase
import com.kgit2.model.toList
import com.kgit2.proxy.ProxyOptions
import com.kgit2.remote.RemoteCallbacks
import com.kgit2.remote.RemoteRedirect
import kotlinx.cinterop.*
import libgit2.git_fetch_options

@Raw(
    base = git_fetch_options::class,
)
class FetchOptions(
    raw: FetchOptionsRaw = FetchOptionsRaw(),
) : GitBase<git_fetch_options, FetchOptionsRaw>(raw) {
    constructor(memory: Memory, handler: FetchOptionsPointer) : this(FetchOptionsRaw(memory, handler))

    val callbacks: RemoteCallbacks = RemoteCallbacks(Memory(), raw.handler.pointed.callbacks.ptr)

    val proxy: ProxyOptions = ProxyOptions(Memory(), raw.handler.pointed.proxy_opts.ptr)

    var prune: FetchPrune = FetchPrune.fromRaw(raw.handler.pointed.prune)
        set(value) {
            field = value
            raw.handler.pointed.prune = value.value
        }

    var updateFetchHead = raw.handler.pointed.update_fetchhead
        set(value) {
            field = value
            raw.handler.pointed.update_fetchhead = value
        }

    var downloadTags: AutoTagOption = AutoTagOption.fromRaw(raw.handler.pointed.download_tags)
        set(value) {
            field = value
            raw.handler.pointed.download_tags = value.value
        }

    var followRedirects: RemoteRedirect = RemoteRedirect.fromRaw(raw.handler.pointed.follow_redirects)
        set(value) {
            field = value
            raw.handler.pointed.follow_redirects = value.value
        }

    private val customHeaders: MutableList<String> = raw.handler.pointed.custom_headers.ptr.toList().toMutableList()

    fun customHeaders(): List<String> = customHeaders.toList()

    fun customHeaders(process: MutableList<String>.() -> Unit) {
        process(customHeaders)
        memScoped {
            raw.handler.pointed.custom_headers.strings = customHeaders.toCStringArray(this)
        }
        raw.handler.pointed.custom_headers.count = customHeaders.size.convert()
    }
}

