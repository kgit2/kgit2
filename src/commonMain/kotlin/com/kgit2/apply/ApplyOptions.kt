package com.kgit2.apply

import com.kgit2.annotations.Raw
import com.kgit2.diff.DiffDelta
import com.kgit2.diff.DiffHunk
import com.kgit2.memory.GitBase
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.pointed
import kotlinx.cinterop.staticCFunction
import libgit2.GIT_APPLY_OPTIONS_VERSION
import libgit2.git_apply_options
import libgit2.git_apply_options_init
import kotlin.native.internal.Cleaner
import kotlin.native.internal.createCleaner

@Raw(
    base = git_apply_options::class,
)
class ApplyOptions(
    raw: ApplyOptionsRaw = ApplyOptionsRaw(initial = {
        git_apply_options_init(this, GIT_APPLY_OPTIONS_VERSION)
    }),
) : GitBase<git_apply_options, ApplyOptionsRaw>(raw) {
    inner class CallbackPayload {
        var deltaCallback: ApplyDeltaCallback? = null
        var hunkCallback: ApplyHunkCallback? = null
    }

    private val callbackPayload = CallbackPayload()
    private val stableRef = StableRef.create(callbackPayload)

    init {
        raw.handler.pointed.payload = stableRef.asCPointer()
    }

    override val cleaner: Cleaner = createCleaner(raw to stableRef) {
        it.second.dispose()
        it.first.free()
    }

    var deltaCallback: ApplyDeltaCallback?
        get() = callbackPayload.deltaCallback
        set(value) {
            callbackPayload.deltaCallback = value
            if (value != null) {
                raw.handler.pointed.delta_cb = staticCFunction { delta, payload ->
                    payload!!.asStableRef<CallbackPayload>().get()
                        .deltaCallback!!.invoke(DiffDelta(handler = delta!!)).value
                }
            }
        }

    var hunkCallback: ApplyHunkCallback?
        get() = callbackPayload.hunkCallback
        set(value) {
            callbackPayload.hunkCallback = value
            if (value != null) {
                raw.handler.pointed.hunk_cb = staticCFunction { hunk, payload ->
                    payload!!.asStableRef<CallbackPayload>().get()
                        .hunkCallback!!.invoke(DiffHunk(handler = hunk!!)).value
                }
            }
        }
}
