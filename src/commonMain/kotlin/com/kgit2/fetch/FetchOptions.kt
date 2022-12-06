package com.kgit2.fetch

import com.kgit2.annotations.Raw
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.common.option.mutually.AutoTagOption
import com.kgit2.memory.RawWrapper
import com.kgit2.model.StrArray
import com.kgit2.proxy.ProxyOptions
import com.kgit2.remote.RemoteCallbacks
import com.kgit2.remote.RemoteRedirect
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import libgit2.GIT_FETCH_OPTIONS_VERSION
import libgit2.git_fetch_options
import libgit2.git_fetch_options_init

@Raw(
    base = git_fetch_options::class,
)
class FetchOptions(
    raw: FetchOptionsRaw = FetchOptionsRaw(initial = {
        git_fetch_options_init(this, GIT_FETCH_OPTIONS_VERSION).errorCheck()
    }),
) : RawWrapper<git_fetch_options, FetchOptionsRaw>(raw) {
    constructor(memory: Memory, handler: FetchOptionsPointer) : this(FetchOptionsRaw(memory, handler))

    val remoteCallbacks: RemoteCallbacks = RemoteCallbacks(Memory(), raw.handler.pointed.callbacks.ptr)

    val proxyOptions: ProxyOptions = ProxyOptions(Memory(), raw.handler.pointed.proxy_opts.ptr)

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

    var followRedirects: RemoteRedirect? = runCatching { RemoteRedirect.from(raw.handler.pointed.follow_redirects) }.getOrNull()
        set(value) {
            field = value
            value?.let { raw.handler.pointed.follow_redirects = it.value }
        }

    val customHeaders: StrArray = StrArray(Memory(), raw.handler.pointed.custom_headers)
}

