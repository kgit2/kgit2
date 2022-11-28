package com.kgit2.cherrypick

import com.kgit2.annotations.Raw
import com.kgit2.checkout.CheckoutOptions
import com.kgit2.checkout.CheckoutOptionsRaw
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import com.kgit2.merge.MergeOptions
import com.kgit2.merge.MergeOptionsRaw
import kotlinx.cinterop.pointed
import libgit2.GIT_CHERRYPICK_OPTIONS_VERSION
import libgit2.git_cherrypick_options
import libgit2.git_cherrypick_options_init

@Raw(
    base = git_cherrypick_options::class,
)
class CherrypickOptions(
    raw: CherrypickOptionsRaw = CherrypickOptionsRaw(initial = {
        git_cherrypick_options_init(this, GIT_CHERRYPICK_OPTIONS_VERSION)
    }),
) : GitBase<git_cherrypick_options, CherrypickOptionsRaw>(raw) {
    var mainLine: UInt = raw.handler.pointed.mainline
        set(value) {
            raw.handler.pointed.mainline = value
            field = value
        }

    val mergeOptions: MergeOptions = MergeOptions(raw = MergeOptionsRaw(Memory(), raw.handler.pointed.merge_opts))

    val checkoutOptions: CheckoutOptions = CheckoutOptions(raw = CheckoutOptionsRaw(Memory(), raw.handler.pointed.checkout_opts))
}
