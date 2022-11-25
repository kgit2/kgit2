package com.kgit2.checkout

import com.kgit2.callback.CheckoutNotifyCallback
import com.kgit2.callback.CheckoutProgressCallback
import com.kgit2.common.error.errorCheck
import com.kgit2.common.error.toBoolean
import com.kgit2.common.error.toInt
import com.kgit2.common.memory.Memory
import com.kgit2.common.option.mutually.FileMode
import com.kgit2.common.option.mutually.FileOpenFlags
import com.kgit2.diff.DiffFile
import com.kgit2.memory.Raw
import com.kgit2.memory.GitBase
import com.kgit2.model.toList
import kotlinx.cinterop.*
import libgit2.GIT_CHECKOUT_OPTIONS_VERSION
import libgit2.git_checkout_options
import libgit2.git_checkout_options_init

typealias CheckoutOptionsPointer = CPointer<git_checkout_options>

typealias CheckoutOptionsSecondaryPointer = CPointerVar<git_checkout_options>

typealias CheckoutOptionsInitial = CheckoutOptionsPointer.(Memory) -> Unit

class CheckoutOptionsRaw(
    memory: Memory = Memory(),
    handler: CheckoutOptionsPointer = memory.alloc<git_checkout_options>().ptr,
) : Raw<git_checkout_options>(memory, handler) {
    init {
        runCatching {
            git_checkout_options_init(handler.getPointer(memory), GIT_CHECKOUT_OPTIONS_VERSION).errorCheck()
        }.onFailure {
            memory.free()
        }.getOrThrow()
    }
}

class CheckoutOptions(
    raw: CheckoutOptionsRaw = CheckoutOptionsRaw(),
) : GitBase<git_checkout_options, CheckoutOptionsRaw>(raw) {
    constructor(memory: Memory, handler: CheckoutOptionsPointer) : this(CheckoutOptionsRaw(memory, handler))

    var strategy: CheckoutStrategyOpts = CheckoutStrategyOpts(raw.handler.pointed.checkout_strategy)
        set(value) {
            field = value
            raw.handler.pointed.checkout_strategy = value.value
        }

    var disableFilters: Boolean = raw.handler.pointed.disable_filters.toBoolean()
        set(value) {
            field = value
            raw.handler.pointed.disable_filters = value.toInt()
        }

    var dirMode: String = run {
        val modeString = CharArray(3)
        var mod = raw.handler.pointed.dir_mode.toInt()
        modeString[2] = Char(mod and 7 + '0'.code)
        mod = mod shr 3
        modeString[1] = Char(mod and 7 + '0'.code)
        mod = mod shr 3
        modeString[0] = Char(mod and 7 + '0'.code)
        modeString.concatToString()
    }
        set(value) {
            if (value.matches(Regex("[0-7]{3}"))) {
                field = value
                var mod = (0 shl 3) or (value[0].code - '0'.code)
                mod = (mod shl 3) or (value[1].code - '0'.code)
                mod = (mod shl 3) or (value[2].code - '0'.code)
            }
        }

    var fileMode: FileMode = FileMode.fromRaw(raw.handler.pointed.file_mode)
        set(value) {
            field = value
            raw.handler.pointed.file_mode = value.value
        }

    var fileOpenFlags: FileOpenFlags = FileOpenFlags.fromRaw(raw.handler.pointed.file_open_flags)
        set(value) {
            field = value
            raw.handler.pointed.file_open_flags = value.value
        }

    /**
     * Must be a class instance that implements CheckoutNotifyCallback
     * Which can be cast to `Any`
     */
    var notifyCallback: CheckoutNotifyCallback? = null
        set(value) {
            field = value
            raw.handler.pointed.notify_payload = StableRef.create(value as Any).asCPointer()
            raw.handler.pointed.notify_cb = staticCFunction { why, path, baseline, target, workdir, payload ->
                payload?.asStableRef<CheckoutNotifyCallback>()?.get()?.checkoutNotify(
                    CheckoutNotificationType.fromRaw(why),
                    path?.toKString(),
                    DiffFile(Memory(), baseline!!),
                    DiffFile(Memory(), target!!),
                    DiffFile(Memory(), workdir!!),
                )?.value ?: -1
            }
        }

    /**
     * Must be a class instance that implements CheckoutProgressCallback
     * Which can be cast to `Any`
     */
    var progressCallback: CheckoutProgressCallback? = null
        set(value) {
            field = value
            raw.handler.pointed.progress_payload = StableRef.create(value as Any).asCPointer()
            raw.handler.pointed.progress_cb = staticCFunction { path, completedSteps, totalSteps, payload ->
                payload?.asStableRef<CheckoutProgressCallback>()?.get()?.checkoutProgress(
                    path!!.toKString(),
                    completedSteps,
                    totalSteps
                )
            }
        }

    private val paths: MutableList<String> = raw.handler.pointed.paths.ptr.toList().toMutableList()

    fun paths(): List<String> = paths

    fun paths(processor: MutableList<String>.() -> Unit) {
        paths.processor()
        memScoped {
            raw.handler.pointed.paths.strings = paths.toCStringArray(this)
        }
        raw.handler.pointed.paths.count = paths.size.convert()
    }
}
