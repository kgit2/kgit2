package com.kgit2.checkout

import com.kgit2.annotations.Raw
import com.kgit2.common.extend.asStableRef
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.extend.toBoolean
import com.kgit2.common.extend.toInt
import com.kgit2.common.memory.Memory
import com.kgit2.common.option.mutually.FileMode
import com.kgit2.common.option.mutually.FileOpenFlags
import com.kgit2.memory.CallbackAble
import com.kgit2.memory.ICallbacksPayload
import com.kgit2.memory.RawWrapper
import com.kgit2.memory.createCleaner
import com.kgit2.model.StrArray
import kotlinx.cinterop.*
import libgit2.GIT_CHECKOUT_OPTIONS_VERSION
import libgit2.git_checkout_options
import libgit2.git_checkout_options_init
import kotlin.native.internal.Cleaner

@Raw(
    base = git_checkout_options::class,
)
class CheckoutOptions(
    raw: CheckoutOptionsRaw = CheckoutOptionsRaw(initial = {
        git_checkout_options_init(this.getPointer(it), GIT_CHECKOUT_OPTIONS_VERSION).errorCheck()
    }),
) : RawWrapper<git_checkout_options, CheckoutOptionsRaw>(raw),
    CallbackAble<git_checkout_options, CheckoutOptionsRaw, CheckoutOptions.CallbacksPayload> {
    constructor(
        memory: Memory = Memory(),
        handler: CheckoutOptionsPointer,
        initial: CheckoutOptionsInitial? = null,
    ) : this(CheckoutOptionsRaw(memory, handler, initial))

    override val callbacksPayload: CallbacksPayload = CallbacksPayload()

    override val stableRef: StableRef<CallbacksPayload> = callbacksPayload.asStableRef()

    override val cleaner: Cleaner = createCleaner()

    inner class CallbacksPayload : ICallbacksPayload, CheckoutNotifyCallbackPayload, CheckoutProgressCallbackPayload {
        override var checkoutProgressCallback: CheckoutProgressCallback? = null
            set(value) {
                field = value
                raw.handler.pointed.progress_payload = value?.let { stableRef.asCPointer() }
                raw.handler.pointed.progress_cb = value?.let { staticCheckoutProgressCallback }
            }

        override var checkoutNotifyCallback: CheckoutNotifyCallback? = null
            set(value) {
                field = value
                raw.handler.pointed.notify_payload = value?.let { stableRef.asCPointer() }
                raw.handler.pointed.notify_cb = value?.let { staticCheckoutNotifyCallback }
            }
    }

    val strategy: CheckoutStrategyOpts = CheckoutStrategyOpts(raw.handler.pointed.checkout_strategy) {
        raw.handler.pointed.checkout_strategy = it
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
    var notifyCallback: CheckoutNotifyCallback? by callbacksPayload::checkoutNotifyCallback

    /**
     * Must be a class instance that implements CheckoutProgressCallback
     * Which can be cast to `Any`
     */
    var progressCallback: CheckoutProgressCallback? by callbacksPayload::checkoutProgressCallback

    val paths: StrArray = StrArray(Memory(), raw.handler.pointed.paths)
}
