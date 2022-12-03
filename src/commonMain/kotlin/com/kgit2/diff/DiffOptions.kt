package com.kgit2.diff

import com.kgit2.annotations.Raw
import com.kgit2.common.extend.asStableRef
import com.kgit2.common.memory.Memory
import com.kgit2.memory.CallbackAble
import com.kgit2.memory.ICallbacksPayload
import com.kgit2.memory.RawWrapper
import com.kgit2.model.StrArray
import com.kgit2.submodule.SubmoduleIgnore
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.cstr
import kotlinx.cinterop.pointed
import kotlinx.cinterop.toKString
import libgit2.GIT_DIFF_OPTIONS_VERSION
import libgit2.git_diff_options
import libgit2.git_diff_options_init

@Raw(
    base = git_diff_options::class,
)
class DiffOptions(
    raw: DiffOptionsRaw = DiffOptionsRaw(initial = {
        git_diff_options_init(this, GIT_DIFF_OPTIONS_VERSION)
    })
) : RawWrapper<git_diff_options, DiffOptionsRaw>(raw), CallbackAble<git_diff_options, DiffOptionsRaw, DiffOptions.CallbacksPayload> {
    constructor(handler: DiffOptionsPointer) : this(DiffOptionsRaw(Memory(), handler))

    val flags: DiffOptionsFlags = DiffOptionsFlags(raw.handler.pointed.flags) {
        raw.handler.pointed.flags = it
    }

    var ignoreSubmodules: SubmoduleIgnore = SubmoduleIgnore.from(raw.handler.pointed.ignore_submodules)
        set(value) {
            field = value
            raw.handler.pointed.ignore_submodules = value.value
        }

    val pathspec: StrArray = StrArray(Memory(), raw.handler.pointed.pathspec)

    var contextLines: UInt = raw.handler.pointed.context_lines
        set(value) {
            field = value
            raw.handler.pointed.context_lines = value
        }

    var interHunkLines: UInt = raw.handler.pointed.interhunk_lines
        set(value) {
            field = value
            raw.handler.pointed.interhunk_lines = value
        }

    var idAbbrev: UShort = raw.handler.pointed.id_abbrev
        set(value) {
            field = value
            raw.handler.pointed.id_abbrev = value
        }

    var maxSize: Long = raw.handler.pointed.max_size
        set(value) {
            field = value
            raw.handler.pointed.max_size = value
        }

    var oldPrefix: String? = raw.handler.pointed.old_prefix?.toKString()
        set(value) {
            field = value
            value?.let { raw.handler.pointed.old_prefix = it.cstr.getPointer(raw.memory) }
        }

    var newPrefix: String? = raw.handler.pointed.new_prefix?.toKString()
        set(value) {
            field = value
            value?.let { raw.handler.pointed.new_prefix = it.cstr.getPointer(raw.memory) }
        }

    override val callbacksPayload: CallbacksPayload = CallbacksPayload()

    override val stableRef: StableRef<CallbacksPayload> = callbacksPayload.asStableRef()

    init {
        raw.handler.pointed.payload = stableRef.asCPointer()
    }

    inner class CallbacksPayload :
        ICallbacksPayload,
        DiffNotifyCallbackPayload,
        DiffProgressCallbackPayload {
        override var diffProgressCallback: DiffProgressCallback? = null
            set(value) {
                field = value
                raw.handler.pointed.progress_cb = value?.let { staticDiffProgressCallback }
            }

        override var diffNotifyCallback: DiffNotifyCallback? = null
            set(value) {
                field = value
                raw.handler.pointed.notify_cb = value?.let { staticDiffNotifyCallback }
            }
    }

    var progressCallback: DiffProgressCallback? by callbacksPayload::diffProgressCallback

    var notifyCallback: DiffNotifyCallback? by callbacksPayload::diffNotifyCallback
}
