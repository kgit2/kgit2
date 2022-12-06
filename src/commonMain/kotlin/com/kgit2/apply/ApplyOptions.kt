package com.kgit2.apply

import com.kgit2.annotations.Raw
import com.kgit2.memory.CallbackAble
import com.kgit2.memory.ICallbacksPayload
import com.kgit2.memory.RawWrapper
import com.kgit2.memory.createCleaner
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.pointed
import libgit2.GIT_APPLY_OPTIONS_VERSION
import libgit2.git_apply_options
import libgit2.git_apply_options_init
import kotlin.native.internal.Cleaner

@Raw(
    base = git_apply_options::class,
)
class ApplyOptions(
    raw: ApplyOptionsRaw = ApplyOptionsRaw(initial = {
        git_apply_options_init(this, GIT_APPLY_OPTIONS_VERSION)
    }),
    initial: ApplyOptions.() -> Unit = {},
) : RawWrapper<git_apply_options, ApplyOptionsRaw>(raw),
    CallbackAble<git_apply_options, ApplyOptionsRaw, ApplyOptions.CallbackPayload> {

    override val callbacksPayload: CallbackPayload = CallbackPayload()
    override val stableRef: StableRef<CallbackPayload> = StableRef.create(callbacksPayload)

    var deltaCallback: ApplyDeltaCallback? by callbacksPayload::applyDeltaCallback
    var hunkCallback: ApplyHunkCallback? by callbacksPayload::applyHunkCallback

    init {
        raw.handler.pointed.payload = stableRef.asCPointer()
        this.initial()
    }

    override val cleaner: Cleaner = createCleaner()

    inner class CallbackPayload : ICallbacksPayload, ApplyDeltaCallbackPayload, ApplyHunkCallbackPayload {
        override var applyDeltaCallback: ApplyDeltaCallback? = null
            set(value) {
                field = value
                raw.handler.pointed.delta_cb = value?.let {
                    staticApplyDeltaCallback
                }
            }

        override var applyHunkCallback: ApplyHunkCallback? = null
            set(value) {
                field = value
                raw.handler.pointed.hunk_cb = value?.let {
                    staticApplyHunkCallback
                }
            }
    }
}
