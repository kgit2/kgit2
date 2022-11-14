package com.kgit2.repository

import com.kgit2.callback.RemoteCreateCallback
import com.kgit2.callback.RepositoryCreateCallback
import com.kgit2.checkout.CheckoutOptions
import com.kgit2.fetch.FetchOptions
import kotlinx.cinterop.Arena
import kotlinx.cinterop.alloc
import kotlinx.cinterop.cstr
import kotlinx.cinterop.ptr
import libgit2.GIT_CLONE_OPTIONS_VERSION
import libgit2.git_clone_init_options
import libgit2.git_clone_options

class CloneOptionsBuilder {
    var checkoutOptions: CheckoutOptions? = null
    var fetchOptions: FetchOptions? = null
    var bare: Boolean = false
    var local: CloneLocalOpts? = null
    var checkoutBranch: String? = null
    var repositoryCreateCallback: RepositoryCreateCallback? = null
    var remoteCreateCallback: RemoteCreateCallback? = null

    fun build(): git_clone_options {
        val arena = Arena()
        val raw = arena.alloc<git_clone_options>()
        git_clone_init_options(raw.ptr, GIT_CLONE_OPTIONS_VERSION)
        raw.bare = when (bare) {
            true -> 1
            false -> 0
        }
        local?.let { raw.local = it.value }
        checkoutBranch?.let { raw.checkout_branch = it.cstr.getPointer(arena) }
        // raw.checkout_opts = arena.alloc<git_checkout_options>()
        return raw
    }
}
