package com.kgit2.remote

import com.kgit2.annotations.Raw
import com.kgit2.certificate.Cert
import com.kgit2.checkout.IndexerProgressCallback
import com.kgit2.common.error.GitErrorCode
import com.kgit2.common.extend.toInt
import com.kgit2.common.memory.Memory
import com.kgit2.credential.CertificateCheckCallback
import com.kgit2.credential.Credential
import com.kgit2.credential.CredentialAcquireCallback
import com.kgit2.credential.CredentialType
import com.kgit2.fetch.Direction
import com.kgit2.index.IndexerProgress
import com.kgit2.memory.GitBase
import com.kgit2.oid.Oid
import com.kgit2.push.PushNegotiationCallback
import com.kgit2.push.PushTransferProgressCallback
import com.kgit2.push.PushUpdate
import com.kgit2.push.PushUpdateReferenceCallback
import com.kgit2.transport.Transport
import com.kgit2.transport.TransportCallback
import kotlinx.cinterop.*
import libgit2.GIT_REMOTE_CALLBACKS_VERSION
import libgit2.git_remote_callbacks
import libgit2.git_remote_init_callbacks
import kotlin.native.internal.Cleaner
import kotlin.native.internal.createCleaner

@Raw(
    base = git_remote_callbacks::class,
)
class RemoteCallbacks(
    raw: RemoteCallbacksRaw = RemoteCallbacksRaw(initial = {
        git_remote_init_callbacks(this, GIT_REMOTE_CALLBACKS_VERSION)
    }),
) : GitBase<git_remote_callbacks, RemoteCallbacksRaw>(raw) {
    constructor(memory: Memory, handler: CPointer<git_remote_callbacks>) : this(RemoteCallbacksRaw(memory, handler))

    inner class CallbackPayload {
        var sidebandProgress: TransportMessageCallback? = null
        var completion: RemoteCompletionCallback? = null
        var credentials: CredentialAcquireCallback? = null
        var certificateCheck: CertificateCheckCallback? = null
        var transferProgress: IndexerProgressCallback? = null
        var updateTips: UpdateTipsCallback? = null
        var pushTransferProgress: PushTransferProgressCallback? = null
        var pushUpdateReference: PushUpdateReferenceCallback? = null
        var pushNegotiation: PushNegotiationCallback? = null
        var transport: TransportCallback? = null
        var remoteReady: RemoteReadyCallback? = null
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
    /**
     * Textual progress from the remote. Text send over the
     * progress side-band will be passed to this function (this is
     * the 'counting objects' output).
     */
    var sidebandProgress: TransportMessageCallback? = null
        set(value) {
            field = value
            raw.handler.pointed.sideband_progress = value?.let {
                staticCFunction { message, _, payload ->
                    val callback = payload!!.asStableRef<CallbackPayload>().get()
                    callback.sidebandProgress!!.invoke(message!!.toKString()).toInt()
                }
            }
        }

    /**
     * Completion is called when different parts of the download
     * process are done (currently unused).
     */
    var completion: RemoteCompletionCallback? = null
        set(value) {
            field = value
            raw.handler.pointed.completion = value?.let {
                staticCFunction { type, payload ->
                    val callback = payload!!.asStableRef<CallbackPayload>().get()
                    callback.completion!!.invoke(RemoteCompletionType.fromRaw(type)).value
                }
            }
        }

    /**
     * This will be called if the remote host requires
     * authentication in order to connect to it.
     *
     * Returning GIT_PASSTHROUGH will make libgit2 behave as
     * though this field isn't set.
     */
    var credentials: CredentialAcquireCallback? = null
        set(value) {
            field = value
            raw.handler.pointed.credentials = value?.let {
                staticCFunction { cred, url, usernameFromUrl, allowedTypes, payload ->
                    val callback = payload!!.asStableRef<CallbackPayload>().get()
                    val credential = Credential(Memory(), cred!!.pointed.value!!)
                    callback.credentials!!.invoke(
                        credential,
                        url!!.toKString(),
                        usernameFromUrl!!.toKString(),
                        CredentialType(allowedTypes)
                    ).value
                }
            }
        }

    /**
     * If cert verification fails, this will be called to let the
     * user make the final decision of whether to allow the
     * connection to proceed. Returns 0 to allow the connection
     * or a negative value to indicate an error.
     */
    var certificateCheck: CertificateCheckCallback? = null
        set(value) {
            field = value
            raw.handler.pointed.certificate_check = value?.let {
                staticCFunction { cert, valid, host, payload ->
                    val callback = payload!!.asStableRef<CallbackPayload>().get()
                    callback.certificateCheck!!.invoke(Cert(Memory(), cert!!), valid == 1, host!!.toKString()).value
                }
            }
        }

    /**
     * During the download of new data, this will be regularly
     * called with the current count of progress done by the
     * indexer.
     */
    var transferProgress: IndexerProgressCallback? = null
        set(value) {
            field = value
            raw.handler.pointed.transfer_progress = value?.let {
                staticCFunction { stats, payload ->
                    val callback = payload!!.asStableRef<CallbackPayload>().get()
                    val progress = IndexerProgress(
                        stats!!.pointed.total_objects,
                        stats.pointed.indexed_objects,
                        stats.pointed.received_objects,
                        stats.pointed.local_objects,
                        stats.pointed.total_deltas,
                        stats.pointed.indexed_deltas,
                        stats.pointed.received_bytes
                    )
                    callback.transferProgress!!.invoke(progress).value
                }
            }
        }

    /**
     * Each time a reference is updated locally, this function
     * will be called with information about it.
     */
    var updateTips: UpdateTipsCallback? = null
        set(value) {
            field = value
            raw.handler.pointed.update_tips = value?.let {
                staticCFunction { refname, a, b, payload ->
                    val callback = payload!!.asStableRef<CallbackPayload>().get()
                    callback.updateTips!!.invoke(refname!!.toKString(), Oid(Memory(), a!!), Oid(Memory(), b!!)).value
                }
            }
        }

    /**
     * Function to call with progress information during the
     * upload portion of a push. Be aware that this is called
     * inline with pack building operations, so performance may be
     * affected.
     */
    var pushTransferProgress: PushTransferProgressCallback? = null
        set(value) {
            field = value
            raw.handler.pointed.push_transfer_progress = value?.let {
                staticCFunction { current, total, bytes, payload ->
                    val callback = payload!!.asStableRef<CallbackPayload>().get()
                    callback.pushTransferProgress!!.invoke(current, total, bytes).value
                }
            }
        }

    var pushUpdateReference: PushUpdateReferenceCallback? = null
        set(value) {
            field = value
            raw.handler.pointed.push_update_reference = value?.let {
                staticCFunction { refName, status, payload ->
                    val callback = payload!!.asStableRef<CallbackPayload>().get()
                    callback.pushUpdateReference!!.invoke(refName!!.toKString(), status!!.toKString()).value
                }
            }
        }

    /**
     * Called once between the negotiation step and the upload. It
     * provides information about what updates will be performed.
     */
    var pushNegotiation: PushNegotiationCallback? = null
        set(value) {
            field = value
            raw.handler.pointed.push_negotiation = value?.let {
                staticCFunction { updates, size, payload ->
                    val callback = payload!!.asStableRef<CallbackPayload>().get()
                    val updateList = MutableList(size.toInt()) {
                        val update = updates!![it]!!.pointed
                        PushUpdate(
                            update.src_refname!!.toKString(),
                            update.dst_refname!!.toString(),
                            Oid(Memory(), update.src.ptr),
                            Oid(Memory(), update.dst.ptr)
                        )
                    }
                    callback.pushNegotiation!!.invoke(updateList).value
                }
            }
        }

    /**
     * Create the transport to use for this operation. Leave NULL
     * to auto-detect.
     */
    var transport: TransportCallback? = null
        set(value) {
            field = value
            raw.handler.pointed.transport = value?.let {
                staticCFunction { transport, remote, payload ->
                    val callback = payload!!.asStableRef<CallbackPayload>().get()
                    callback.transport!!.invoke(
                        Transport(Memory(), transport!!.pointed.value!!),
                        Remote(Memory(), remote!!)
                    ).value
                }
            }
        }

    /**
     * Callback when the remote is ready to connect.
     */
    var remoteReady: RemoteReadyCallback? = null
        set(value) {
            field = value
            raw.handler.pointed.remote_ready = value?.let {
                staticCFunction { remote, direction, payload ->
                    val callback = payload!!.asStableRef<CallbackPayload>().get()
                    callback.remoteReady!!.invoke(Remote(Memory(), remote!!), Direction.fromRaw(direction.toUInt())).value
                }
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

/**
 * Completion is called when different parts of the download
 * process are done (currently unused).
 */
typealias RemoteCompletionCallback = (type: RemoteCompletionType) -> GitErrorCode

/**
 * Callback for the user's custom update tips.
 *
 * @param refname The name of the reference that was updated
 * @param a The old OID for the reference
 * @param b The new OID for the reference
 * @return 0 to proceed with the update, < 0 to fail the update
 */
typealias UpdateTipsCallback = (refname: String, a: Oid, b: Oid) -> GitErrorCode

/**
 * Callback for the user's custom remote ready.
 *
 * @param remote The remote to be used
 * @param direction GIT_DIRECTION_FETCH or GIT_DIRECTION_PUSH
 * @return 0 to proceed with the push, < 0 to fail the push
 */
typealias RemoteReadyCallback = (remote: Remote, direction: Direction) -> GitErrorCode
