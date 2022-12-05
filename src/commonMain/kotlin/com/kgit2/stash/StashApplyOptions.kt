package com.kgit2.stash

import com.kgit2.annotations.Raw
import com.kgit2.checkout.CheckoutOptions
import com.kgit2.checkout.CheckoutOptionsRaw
import com.kgit2.common.extend.asStableRef
import com.kgit2.common.memory.Memory
import com.kgit2.memory.CallbackAble
import com.kgit2.memory.ICallbacksPayload
import com.kgit2.memory.RawWrapper
import com.kgit2.memory.createCleaner
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.pointed
import libgit2.GIT_STASH_APPLY_OPTIONS_VERSION
import libgit2.git_stash_apply_options
import libgit2.git_stash_apply_options_init
import kotlin.native.internal.Cleaner

@Raw(
    base = git_stash_apply_options::class,
)
class StashApplyOptions(
    raw: StashApplyOptionsRaw = StashApplyOptionsRaw(initial = {
        git_stash_apply_options_init(this, GIT_STASH_APPLY_OPTIONS_VERSION)
    }),
    initial: StashApplyOptions.() -> Unit = {},
) : RawWrapper<git_stash_apply_options, StashApplyOptionsRaw>(raw),
    CallbackAble<git_stash_apply_options, StashApplyOptionsRaw, StashApplyOptions.CallbacksPayload> {

    override val callbacksPayload: CallbacksPayload = CallbacksPayload()

    override val stableRef: StableRef<CallbacksPayload> = callbacksPayload.asStableRef()

    override val cleaner: Cleaner = createCleaner()

    var flags: StashApplyFlags = StashApplyFlags.from(raw.handler.pointed.flags)
        set(value) {
            field = value
            raw.handler.pointed.flags = value.value
        }

    val checkoutOptions: CheckoutOptions =
        CheckoutOptions(CheckoutOptionsRaw(Memory(), raw.handler.pointed.checkout_options))

    var stashApplyProgressCallback: StashApplyProgressCallback? by callbacksPayload::stashApplyProgressCallback

    init {
        raw.handler.pointed.progress_payload = stableRef.asCPointer()
        this.initial()
    }

    inner class CallbacksPayload : ICallbacksPayload, StashApplyProgressCallbackPayload {
        override var stashApplyProgressCallback: StashApplyProgressCallback? = null
            set(value) {
                field = value
                raw.handler.pointed.progress_cb = value?.let { staticStashApplyProgressCallback }
            }
    }
}
