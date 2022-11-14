package com.kgit2.proxy

import libgit2.git_proxy_t

enum class ProxyKind(val value: git_proxy_t) {
    None(git_proxy_t.GIT_PROXY_NONE),
    Auto(git_proxy_t.GIT_PROXY_AUTO),
    Specified(git_proxy_t.GIT_PROXY_SPECIFIED),
}
