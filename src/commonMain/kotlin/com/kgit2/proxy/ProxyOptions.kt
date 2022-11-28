package com.kgit2.proxy

import com.kgit2.annotations.Raw
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import kotlinx.cinterop.cstr
import kotlinx.cinterop.pointed
import libgit2.GIT_PROXY_OPTIONS_VERSION
import libgit2.git_proxy_options
import libgit2.git_proxy_options_init

@Raw(
    base = git_proxy_options::class
)
class ProxyOptions(
    raw: ProxyOptionsRaw = ProxyOptionsRaw(initial =  {
        git_proxy_options_init(this, GIT_PROXY_OPTIONS_VERSION).errorCheck()
    }),
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
