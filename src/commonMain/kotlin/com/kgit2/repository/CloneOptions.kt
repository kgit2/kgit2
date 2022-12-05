package com.kgit2.repository

import com.kgit2.annotations.Raw
import com.kgit2.checkout.CheckoutOptions
import com.kgit2.common.extend.asStableRef
import com.kgit2.common.extend.toBoolean
import com.kgit2.common.extend.toInt
import com.kgit2.common.memory.Memory
import com.kgit2.fetch.FetchOptions
import com.kgit2.memory.CallbackAble
import com.kgit2.memory.ICallbacksPayload
import com.kgit2.memory.RawWrapper
import com.kgit2.memory.createCleaner
import com.kgit2.remote.Remote
import kotlinx.cinterop.*
import libgit2.git_clone_options
import kotlin.native.internal.Cleaner

@Raw(
    base = git_clone_options::class,
)
class CloneOptions(
    raw: CloneOptionsRaw = CloneOptionsRaw(),
    initial: CloneOptions.() -> Unit = {},
) :
    RawWrapper<git_clone_options, CloneOptionsRaw>(raw),
    CallbackAble<git_clone_options, CloneOptionsRaw, CloneOptions.CallbacksPayload> {
    override val callbacksPayload = CallbacksPayload()

    override val stableRef = callbacksPayload.asStableRef()

    override val cleaner: Cleaner = createCleaner()

    val checkoutOptions: CheckoutOptions = CheckoutOptions(Memory(), raw.handler.pointed.checkout_opts.ptr)

    val fetchOptions: FetchOptions = FetchOptions(Memory(), raw.handler.pointed.fetch_opts.ptr)

    var bare: Boolean = false
        set(value) {
            raw.handler.pointed.bare = value.toInt()
            field = value
        }

    var local: CloneLocalOpts = CloneLocalOpts.fromRaw(raw.handler.pointed.local)
        set(value) {
            raw.handler.pointed.local = value.value
            field = value
        }

    var checkoutBranch: String? = raw.handler.pointed.checkout_branch?.toKString()
        set(value) {
            raw.handler.pointed.checkout_branch = value?.cstr?.getPointer(raw.memory)
            field = value
        }

    var repositoryCreateCallback: RepositoryCreateCallback? by callbacksPayload::repositoryCreateCallback

    var remoteCreateCallback: RemoteCreateCallback? by callbacksPayload::remoteCreateCallback

    init {
        this.initial()
        raw.handler.pointed.repository_cb_payload = stableRef.asCPointer()
        raw.handler.pointed.remote_cb_payload = stableRef.asCPointer()
    }

    inner class CallbacksPayload : ICallbacksPayload, RepositoryCreateCallbackPayload, RemoteCreateCallbackPayload {
        override var repositoryCreateCallback: RepositoryCreateCallback? = null
            set(value) {
                field = value
                raw.handler.pointed.repository_cb = value?.let { staticRepositoryCreateCallback }
            }
        override var remoteCreateCallback: RemoteCreateCallback? = null
            set(value) {
                field = value
                raw.handler.pointed.remote_cb = value?.let { staticRemoteCreateCallback }
            }
    }
}
