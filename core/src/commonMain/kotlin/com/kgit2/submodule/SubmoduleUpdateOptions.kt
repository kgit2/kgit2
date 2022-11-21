package com.kgit2.submodule

import com.kgit2.checkout.CheckoutOptions
import com.kgit2.common.error.toInt
import com.kgit2.common.memory.Memory
import com.kgit2.fetch.FetchOptions
import com.kgit2.memory.Raw
import com.kgit2.memory.GitBase
import kotlinx.cinterop.*
import libgit2.GIT_SUBMODULE_UPDATE_OPTIONS_VERSION
import libgit2.git_submodule_update_options
import libgit2.git_submodule_update_options_init

typealias SubmoduleUpdateOptionsPointer = CPointer<git_submodule_update_options>

typealias SubmoduleUpdateOptionsSecondaryPointer = CPointerVar<git_submodule_update_options>

class SubmoduleUpdateOptionsRaw(
    memory: Memory = Memory(),
    handler: SubmoduleUpdateOptionsPointer = memory.alloc<git_submodule_update_options>().ptr,
) : Raw<git_submodule_update_options>(memory, handler) {
    init {
        git_submodule_update_options_init(handler, GIT_SUBMODULE_UPDATE_OPTIONS_VERSION)
    }
}

class SubmoduleUpdateOptions(raw: SubmoduleUpdateOptionsRaw = SubmoduleUpdateOptionsRaw()) : GitBase<git_submodule_update_options, SubmoduleUpdateOptionsRaw>(raw) {
    constructor(memory: Memory, handler: SubmoduleUpdateOptionsPointer) : this(SubmoduleUpdateOptionsRaw(memory, handler))

    val checkoutOptions: CheckoutOptions = CheckoutOptions(Memory(), raw.handler.pointed.checkout_opts.ptr)

    val fetchOptions: FetchOptions = FetchOptions(Memory(), raw.handler.pointed.fetch_opts.ptr)

    fun allowFetch(value: Boolean) {
        raw.handler.pointed.allow_fetch = value.toInt()
    }
}
