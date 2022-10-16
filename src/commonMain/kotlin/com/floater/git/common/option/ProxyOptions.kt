package com.floater.git.common.option

import kotlinx.cinterop.internal.CCall
import libgit2.git_proxy_t

data class ProxyOptions(
    var url: String? = null,
    var proxyKind: ProxyKind = ProxyKind.GIT_PROXY_NONE,
)

enum class ProxyKind(val value: git_proxy_t) {
    GIT_PROXY_NONE(git_proxy_t.GIT_PROXY_NONE),
    GIT_PROXY_AUTO(git_proxy_t.GIT_PROXY_AUTO),
    GIT_PROXY_SPECIFIED(git_proxy_t.GIT_PROXY_SPECIFIED),
}
