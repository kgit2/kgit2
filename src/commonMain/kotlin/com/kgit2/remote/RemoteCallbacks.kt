package com.kgit2.remote

import cnames.structs.git_remote
import com.kgit2.annotations.Raw
import com.kgit2.checkout.IndexerProgressCallback
import com.kgit2.checkout.IndexerProgressCallbackPayload
import com.kgit2.checkout.staticIndexerProgressCallback
import com.kgit2.common.callback.CallbackResult
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.extend.toInt
import com.kgit2.common.memory.Memory
import com.kgit2.credential.*
import com.kgit2.fetch.Direction
import com.kgit2.memory.ICallbacksPayload
import com.kgit2.memory.RawWrapper
import com.kgit2.oid.Oid
import com.kgit2.push.*
import com.kgit2.transport.TransportCallback
import com.kgit2.transport.TransportCallbackPayload
import com.kgit2.transport.staticTransportCallback
import kotlinx.cinterop.*
import libgit2.*
import kotlin.native.internal.Cleaner
import kotlin.native.internal.createCleaner

@Raw(
    base = git_remote_callbacks::class,
)
class RemoteCallbacks(
    raw: RemoteCallbacksRaw = RemoteCallbacksRaw(initial = {
        git_remote_init_callbacks(this, GIT_REMOTE_CALLBACKS_VERSION).errorCheck()
    }),
    initial: RemoteCallbacks.() -> Unit = {},
) : RawWrapper<git_remote_callbacks, RemoteCallbacksRaw>(raw) {
    constructor(memory: Memory, handler: CPointer<git_remote_callbacks>) : this(RemoteCallbacksRaw(memory, handler))

    private val callbackPayload = CallbackPayload()

    private val stableRef = StableRef.create(callbackPayload)

    /**
     * Textual progress from the remote. Text send over the
     * progress side-band will be passed to this function (this is
     * the 'counting objects' output).
     */
    var sidebandProgress: TransportMessageCallback? by callbackPayload::transportMessageCallback

    /**
     * Completion is called when different parts of the download
     * process are done (currently unused).
     */
    var completion: RemoteCompletionCallback? by callbackPayload::remoteCompletionCallback

    /**
     * This will be called if the remote host requires
     * authentication in order to connect to it.
     *
     * Returning GIT_PASSTHROUGH will make libgit2 behave as
     * though this field isn't set.
     */
    var credentialCallback: CredentialAcquireCallback? by callbackPayload::credentialAcquireCallback

    /**
     * If cert verification fails, this will be called to let the
     * user make the final decision of whether to allow the
     * connection to proceed. Returns 0 to allow the connection
     * or a negative value to indicate an error.
     */
    var certificateCheck: CertificateCheckCallback? by callbackPayload::certificateCheckCallback

    /**
     * During the download of new data, this will be regularly
     * called with the current count of progress done by the
     * indexer.
     */
    var transferProgress: IndexerProgressCallback? by callbackPayload::indexerProgressCallback

    /**
     * Each time a reference is updated locally, this function
     * will be called with information about it.
     */
    var updateTips: UpdateTipsCallback? by callbackPayload::updateTipsCallback

    /**
     * Function to call with progress information during the
     * upload portion of a push. Be aware that this is called
     * inline with pack building operations, so performance may be
     * affected.
     */
    var pushTransferProgress: PushTransferProgressCallback? by callbackPayload::pushTransferProgressCallback

    var pushUpdateReference: PushUpdateReferenceCallback? by callbackPayload::pushUpdateReferenceCallback

    /**
     * Called once between the negotiation step and the upload. It
     * provides information about what updates will be performed.
     */
    var pushNegotiation: PushNegotiationCallback? by callbackPayload::pushNegotiationCallback

    /**
     * Create the transport to use for this operation. Leave NULL
     * to auto-detect.
     */
    var transport: TransportCallback? by callbackPayload::transportCallback

    /**
     * Callback when the remote is ready to connect.
     */
    var remoteReady: RemoteReadyCallback? by callbackPayload::remoteReadyCallback

    init {
        raw.handler.pointed.payload = stableRef.asCPointer()
        this.initial()
    }

    override val cleaner: Cleaner = createCleaner(raw to stableRef) {
        it.second.dispose()
        it.first.free()
    }

    inner class CallbackPayload
        : ICallbacksPayload,
        TransportMessageCallbackPayload,
        RemoteCompletionCallbackPayload,
        CredentialAcquireCallbackPayload,
        CertificateCheckCallbackPayload,
        IndexerProgressCallbackPayload,
        UpdateTipsCallbackPayload,
        PushTransferProgressCallbackPayload,
        PushUpdateReferenceCallbackPayload,
        PushNegotiationCallbackPayload,
        TransportCallbackPayload,
        RemoteReadyCallbackPayload {
        override var transportMessageCallback: TransportMessageCallback? = null
            set(value) {
                field = value
                raw.handler.pointed.sideband_progress = value?.let { staticTransportMessageCallback }
            }
        override var remoteCompletionCallback: RemoteCompletionCallback? = null
            set(value) {
                field = value
                raw.handler.pointed.completion = value?.let { staticRemoteCompletionCallback }
            }
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
        override var indexerProgressCallback: IndexerProgressCallback? = null
            set(value) {
                field = value
                raw.handler.pointed.transfer_progress = value?.let { staticIndexerProgressCallback }
            }
        override var updateTipsCallback: UpdateTipsCallback? = null
            set(value) {
                field = value
                raw.handler.pointed.update_tips = value?.let { staticUpdateTipsCallback }
            }
        override var pushTransferProgressCallback: PushTransferProgressCallback? = null
            set(value) {
                field = value
                raw.handler.pointed.push_transfer_progress = value?.let { staticPushTransferProgressCallback }
            }
        override var pushUpdateReferenceCallback: PushUpdateReferenceCallback? = null
            set(value) {
                field = value
                raw.handler.pointed.push_update_reference = value?.let { staticPushUpdateReferenceCallback }
            }
        override var pushNegotiationCallback: PushNegotiationCallback? = null
            set(value) {
                field = value
                raw.handler.pointed.push_negotiation = value?.let { staticPushNegotiationCallback }
            }
        override var transportCallback: TransportCallback? = null
            set(value) {
                field = value
                raw.handler.pointed.transport = value?.let { staticTransportCallback }
            }
        override var remoteReadyCallback: RemoteReadyCallback? = null
            set(value) {
                field = value
                raw.handler.pointed.remote_ready = value?.let { staticRemoteReadyCallback }
            }
    }
}

/**
 * Callback for messages received by the transport.
 *
 * Return a negative value to cancel the network operation.
 *
 * @param message The message from the transport
 */
typealias TransportMessageCallback = (message: String) -> Boolean

interface TransportMessageCallbackPayload {
    var transportMessageCallback: TransportMessageCallback?
}

val staticTransportMessageCallback: git_transport_message_cb =
    staticCFunction {
            message: CPointer<ByteVar>?,
            size: Int,
            payload,
        ->
        val callback = payload!!.asStableRef<TransportMessageCallbackPayload>().get()
        callback.transportMessageCallback!!.invoke(message!!.readBytes(size).toKString()).toInt()
    }

/**
 * Completion is called when different parts of the download
 * process are done (currently unused).
 */
typealias RemoteCompletionCallback = (type: RemoteCompletionType) -> CallbackResult

interface RemoteCompletionCallbackPayload {
    var remoteCompletionCallback: RemoteCompletionCallback?
}

val staticRemoteCompletionCallback: CPointer<CFunction<(git_remote_completion_t, COpaquePointer?) -> Int>> =
    staticCFunction {
            completionType: git_remote_completion_t,
            payload,
        ->
        val callback = payload!!.asStableRef<RemoteCompletionCallbackPayload>().get()
        callback.remoteCompletionCallback!!.invoke(RemoteCompletionType.from(completionType)).value
    }

/**
 * Callback for the user's custom update tips.
 *
 * @param refname The name of the reference that was updated
 * @param a The old OID for the reference
 * @param b The new OID for the reference
 * @return 0 to proceed with the update, < 0 to fail the update
 */
typealias UpdateTipsCallback = (refName: String, a: Oid, b: Oid) -> CallbackResult

interface UpdateTipsCallbackPayload {
    val updateTipsCallback: UpdateTipsCallback?
}

val staticUpdateTipsCallback: CPointer<CFunction<(CPointer<ByteVar>?, CPointer<git_oid>?, CPointer<git_oid>?, COpaquePointer?) -> Int>> =
    staticCFunction {
            refName: CPointer<ByteVar>?,
            a: CPointer<git_oid>?,
            b: CPointer<git_oid>?,
            payload,
        ->
        val callback = payload!!.asStableRef<UpdateTipsCallbackPayload>().get()
        callback.updateTipsCallback!!.invoke(refName!!.toKString(), Oid(Memory(), a!!), Oid(Memory(), b!!)).value
    }

/**
 * Callback for the user's custom remote ready.
 *
 * @param remote The remote to be used
 * @param direction GIT_DIRECTION_FETCH or GIT_DIRECTION_PUSH
 * @return 0 to proceed with the push, < 0 to fail the push
 */
typealias RemoteReadyCallback = (remote: Remote, direction: Direction) -> CallbackResult

interface RemoteReadyCallbackPayload {
    val remoteReadyCallback: RemoteReadyCallback?
}

val staticRemoteReadyCallback: git_remote_ready_cb =
    staticCFunction {
            remote: CPointer<git_remote>?,
            direction: Int,
            payload: COpaquePointer?,
        ->
        val callback = payload!!.asStableRef<RemoteReadyCallbackPayload>().get()
        callback.remoteReadyCallback!!.invoke(
            Remote(Memory(), remote!!),
            Direction.fromRaw(direction.toUInt())
        ).value
    }
