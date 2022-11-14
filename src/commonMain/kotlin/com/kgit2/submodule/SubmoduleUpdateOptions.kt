package com.kgit2.submodule

import com.kgit2.checkout.CheckoutOptions
import com.kgit2.fetch.FetchOptions
import com.kgit2.model.AutoFreeGitBase
import kotlinx.cinterop.*
import libgit2.GIT_SUBMODULE_UPDATE_OPTIONS_VERSION
import libgit2.git_submodule_update_options
import libgit2.git_submodule_update_options_init

class SubmoduleUpdateOptions(
    override val handler: CPointer<git_submodule_update_options>,
    override val arena: Arena,
) : AutoFreeGitBase<CPointer<git_submodule_update_options>> {
    companion object {
        fun initialized(): SubmoduleUpdateOptions {
            val arena = Arena()
            val handler = arena.alloc<git_submodule_update_options>()
            git_submodule_update_options_init(handler.ptr, GIT_SUBMODULE_UPDATE_OPTIONS_VERSION)
            return SubmoduleUpdateOptions(handler.ptr, arena)
        }
    }

    val checkoutOptions: CheckoutOptions = CheckoutOptions(handler.pointed.checkout_opts.ptr, arena)

    val fetchOptions: FetchOptions = FetchOptions(handler.pointed.fetch_opts.ptr, arena)

    fun allowFetch(value: Boolean) {
        handler.pointed.allow_fetch = if (value) 1 else 0
    }
}
