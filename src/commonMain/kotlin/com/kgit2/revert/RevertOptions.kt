package com.kgit2.revert

import com.kgit2.annotations.Raw
import com.kgit2.checkout.CheckoutOptions
import com.kgit2.memory.GitBase
import com.kgit2.merge.MergeOptions
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import libgit2.GIT_REVERT_OPTIONS_VERSION
import libgit2.git_revert_options
import libgit2.git_revert_options_init

@Raw(
    base = git_revert_options::class,
)
class RevertOptions(
    raw: RevertOptionsRaw = RevertOptionsRaw(initial = {
        git_revert_options_init(this, GIT_REVERT_OPTIONS_VERSION)
    }),
) : GitBase<git_revert_options, RevertOptionsRaw>(raw) {
    var mainLine: UInt = raw.handler.pointed.mainline
        set(value) {
            field = value
            raw.handler.pointed.mainline = value
        }

    val mergeOptions: MergeOptions = MergeOptions(handler = raw.handler.pointed.merge_opts.ptr)

    val checkoutOptions: CheckoutOptions = CheckoutOptions(handler = raw.handler.pointed.checkout_opts.ptr)
}
