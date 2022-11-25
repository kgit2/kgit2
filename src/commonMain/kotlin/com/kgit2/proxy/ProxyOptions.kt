package com.kgit2.proxy

import com.kgit2.common.memory.Memory
import com.kgit2.memory.Raw
import com.kgit2.memory.GitBase
import kotlinx.cinterop.*
import libgit2.GIT_PROXY_OPTIONS_VERSION
import libgit2.git_proxy_options
import libgit2.git_proxy_options_init

typealias ProxyOptionsPointer = CPointer<git_proxy_options>

typealias ProxyOptionsSecondaryPointer = CPointerVar<git_proxy_options>

typealias ProxyOptionsInitial = ProxyOptionsSecondaryPointer.(Memory) -> Unit

class ProxyOptionsRaw(
    memory: Memory = Memory(),
    handler: ProxyOptionsPointer = memory.alloc<git_proxy_options>().ptr,
) : Raw<git_proxy_options>(memory, handler) {
    init {
        runCatching {
            git_proxy_options_init(handler, GIT_PROXY_OPTIONS_VERSION)
        }.onFailure {
            memory.free()
        }.getOrThrow()
    }
}

class ProxyOptions(
    raw: ProxyOptionsRaw = ProxyOptionsRaw(),
) : GitBase<git_proxy_options, ProxyOptionsRaw>(raw) {
    constructor(memory: Memory, handler: ProxyOptionsPointer) : this(ProxyOptionsRaw(memory, handler))

    var url: String? = null
        set(value) {
            field = value
            raw.handler.pointed.url = value?.cstr?.getPointer(raw.memory)
        }

    var proxyKind: ProxyKind = ProxyKind.None
        set(value) {
            field = value
            raw.handler.pointed.type = value.value
        }
}
