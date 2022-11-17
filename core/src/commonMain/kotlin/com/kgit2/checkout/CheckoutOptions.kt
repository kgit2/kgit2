package com.kgit2.checkout

import com.kgit2.callback.CheckoutNotifyCallback
import com.kgit2.callback.CheckoutProgressCallback
import com.kgit2.common.option.mutually.FileMode
import com.kgit2.common.option.mutually.FileOpenFlags
import com.kgit2.diff.DiffFile
import com.kgit2.model.AutoFreeGitBase
import com.kgit2.model.toList
import kotlinx.cinterop.*
import libgit2.GIT_CHECKOUT_OPTIONS_VERSION
import libgit2.git_checkout_options
import libgit2.git_checkout_options_init

class CheckoutOptions(
    override val handler: CPointer<git_checkout_options>,
    override val arena: Arena,
) : AutoFreeGitBase<CPointer<git_checkout_options>> {
    companion object {
        fun initialized(): CheckoutOptions {
            val arena = Arena()
            val handler = arena.alloc<git_checkout_options>()
            git_checkout_options_init(handler.ptr, GIT_CHECKOUT_OPTIONS_VERSION)
            return CheckoutOptions(handler.ptr, arena)
        }
    }

    var strategy: CheckoutStrategyOpts = CheckoutStrategyOpts(handler.pointed.checkout_strategy)
        set(value) {
            field = value
            handler.pointed.checkout_strategy = value.value
        }

    var disableFilters: Boolean = handler.pointed.disable_filters == 1
        set(value) {
            field = value
            handler.pointed.disable_filters = if (value) 1 else 0
        }

    var dirMode: String = kotlin.run {
        val modeString = CharArray(3)
        var mod = handler.pointed.dir_mode.toInt()
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

    var fileMode: FileMode = FileMode.fromRaw(handler.pointed.file_mode)
        set(value) {
            field = value
            handler.pointed.file_mode = value.value
        }

    var fileOpenFlags: FileOpenFlags = FileOpenFlags.fromRaw(handler.pointed.file_open_flags)
        set(value) {
            field = value
            handler.pointed.file_open_flags = value.value
        }

    /**
     * Must be a class instance that implements CheckoutNotifyCallback
     * Which can be cast to `Any`
     */
    var notifyCallback: CheckoutNotifyCallback? = null
        set(value) {
            field = value
            handler.pointed.notify_payload = StableRef.create(value as Any).asCPointer()
            handler.pointed.notify_cb = staticCFunction { why, path, baseline, target, workdir, payload ->
                payload?.asStableRef<CheckoutNotifyCallback>()?.get()?.checkoutNotify(
                    CheckoutNotificationType.fromRaw(why),
                    path?.toKString(),
                    DiffFile.fromHandler(baseline!!.pointed, Arena()),
                    DiffFile.fromHandler(target!!.pointed, Arena()),
                    DiffFile.fromHandler(workdir!!.pointed, Arena()),
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
            handler.pointed.progress_payload = StableRef.create(value as Any).asCPointer()
            handler.pointed.progress_cb = staticCFunction { path, completedSteps, totalSteps, payload ->
                payload?.asStableRef<CheckoutProgressCallback>()?.get()?.checkoutProgress(
                    path!!.toKString(),
                    completedSteps,
                    totalSteps
                )
            }
        }

    private val paths: MutableList<String> = handler.pointed.paths.ptr.toList().toMutableList()

    fun paths(): List<String> = paths

    fun paths(processor: MutableList<String>.() -> Unit) {
        paths.processor()
        memScoped {
            handler.pointed.paths.strings = paths.toCStringArray(this)
        }
        handler.pointed.paths.count = paths.size.convert()
    }
}
