package com.kgit2.proxy

import com.kgit2.annotations.Raw
import com.kgit2.common.extend.asStableRef
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.credential.CertificateCheckCallback
import com.kgit2.credential.CertificateCheckCallbackPayload
import com.kgit2.credential.CredentialAcquireCallback
import com.kgit2.credential.CredentialAcquireCallbackPayload
import com.kgit2.credential.staticCertificateCheckCallback
import com.kgit2.credential.staticCredentialAcquireCallback
import com.kgit2.memory.CallbackAble
import com.kgit2.memory.ICallbacksPayload
import com.kgit2.memory.RawWrapper
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.cstr
import kotlinx.cinterop.pointed
import libgit2.GIT_PROXY_OPTIONS_VERSION
import libgit2.git_proxy_options
import libgit2.git_proxy_options_init

@Raw(
    base = git_proxy_options::class
)
class ProxyOptions(
    raw: ProxyOptionsRaw = ProxyOptionsRaw(initial = {
        git_proxy_options_init(this, GIT_PROXY_OPTIONS_VERSION).errorCheck()
    }),
    initial: ProxyOptions.() -> Unit = {}
) : RawWrapper<git_proxy_options, ProxyOptionsRaw>(raw),
    CallbackAble<git_proxy_options, ProxyOptionsRaw, ProxyOptions.CallbacksPayload> {
    constructor(memory: Memory, handler: ProxyOptionsPointer) : this(ProxyOptionsRaw(memory, handler))

    var url: String? = null
        set(value) {
            field = value
            raw.handler.pointed.url = value?.cstr?.getPointer(raw.memory)
        }

    var proxyKind: ProxyKind = ProxyKind.None
        set(value) {
            field = value
            raw.handler.pointed.type = value.value
        }

    override val callbacksPayload: CallbacksPayload = CallbacksPayload()

    override val stableRef: StableRef<CallbacksPayload> = callbacksPayload.asStableRef()

    var credentialAcquireCallback: CredentialAcquireCallback? by callbacksPayload::credentialAcquireCallback

    var certificateCheckCallback: CertificateCheckCallback? by callbacksPayload::certificateCheckCallback

    init {
        raw.handler.pointed.payload = stableRef.asCPointer()
        this.initial()
    }

    inner class CallbacksPayload : ICallbacksPayload, CredentialAcquireCallbackPayload,
        CertificateCheckCallbackPayload {
        override var credentialAcquireCallback: CredentialAcquireCallback? = null
            set(value) {
                field = value
                raw.handler.pointed.credentials = value?.let { staticCredentialAcquireCallback }
            }

        override var certificateCheckCallback: CertificateCheckCallback? = null
            set(value) {
                field = value
                raw.handler.pointed.certificate_check = value?.let { staticCertificateCheckCallback }
            }
    }
}
