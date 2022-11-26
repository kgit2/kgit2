package com.kgit2.submodule

import com.kgit2.annotations.Raw
import com.kgit2.checkout.CheckoutOptions
import com.kgit2.common.error.toInt
import com.kgit2.common.memory.Memory
import com.kgit2.fetch.FetchOptions
import com.kgit2.memory.GitBase
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import libgit2.GIT_SUBMODULE_UPDATE_OPTIONS_VERSION
import libgit2.git_submodule_update_options
import libgit2.git_submodule_update_options_init

@Raw(
    base = git_submodule_update_options::class,
)
class SubmoduleUpdateOptions(
    raw: SubmoduleUpdateOptionsRaw = SubmoduleUpdateOptionsRaw(initial = {
        git_submodule_update_options_init(this, GIT_SUBMODULE_UPDATE_OPTIONS_VERSION)
    }),
) : GitBase<git_submodule_update_options, SubmoduleUpdateOptionsRaw>(raw) {
    constructor(memory: Memory, handler: SubmoduleUpdateOptionsPointer) : this(
        SubmoduleUpdateOptionsRaw(
            memory,
            handler
        )
    )

    val checkoutOptions: CheckoutOptions = CheckoutOptions(Memory(), raw.handler.pointed.checkout_opts.ptr)

    val fetchOptions: FetchOptions = FetchOptions(Memory(), raw.handler.pointed.fetch_opts.ptr)

    fun allowFetch(value: Boolean) {
        raw.handler.pointed.allow_fetch = value.toInt()
    }
}
