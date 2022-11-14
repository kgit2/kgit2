package com.kgit2.proxy

import com.kgit2.model.AutoFreeGitBase
import kotlinx.cinterop.*
import libgit2.git_proxy_options
import libgit2.git_proxy_options_init

class ProxyOptions(
    override val handler: CPointer<git_proxy_options>,
    override val arena: Arena,
) : AutoFreeGitBase<CPointer<git_proxy_options>> {
    companion object {
        fun initialized(): ProxyOptions {
            val arena = Arena()
            val handler = arena.alloc<git_proxy_options>()
            git_proxy_options_init(handler.ptr, 1)
            return ProxyOptions(handler.ptr, arena)
        }
    }

    var url: String? = null
        set(value) {
            field = value
            memScoped {
                handler.pointed.url = value?.cstr?.ptr
            }
        }

    var proxyKind: ProxyKind = ProxyKind.None
        set(value) {
            field = value
            handler.pointed.type = value.value
        }
}
