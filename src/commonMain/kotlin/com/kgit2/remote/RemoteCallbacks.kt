package com.kgit2.remote

import com.kgit2.callback.*
import com.kgit2.callback.payload.IndexerProgress
import com.kgit2.callback.payload.PushUpdate
import com.kgit2.certificate.Cert
import com.kgit2.common.memory.Memory
import com.kgit2.credential.Credential
import com.kgit2.credential.CredentialType
import com.kgit2.fetch.Direction
import com.kgit2.memory.BeforeFree
import com.kgit2.memory.GitBase
import com.kgit2.memory.Raw
import com.kgit2.model.Oid
import com.kgit2.transport.Transport
import kotlinx.cinterop.*
import libgit2.GIT_REMOTE_CALLBACKS_VERSION
import libgit2.git_remote_callbacks
import libgit2.git_remote_init_callbacks

typealias RemoteCallbacksPointer = CPointer<git_remote_callbacks>

typealias RemoteCallbacksSecondaryPointer = CPointerVar<git_remote_callbacks>

typealias RemoteCallbacksInitial = RemoteCallbacksSecondaryPointer.(Memory) -> Unit

class RemoteCallbacksRaw(
    memory: Memory = Memory(),
    handler: CPointer<git_remote_callbacks> = memory.alloc<git_remote_callbacks>().ptr,
) : Raw<git_remote_callbacks>(memory, handler) {
    private val stableRef: StableRef<RemoteCallbacksRaw> = StableRef.create(this)

    init {
        runCatching {
            git_remote_init_callbacks(handler, GIT_REMOTE_CALLBACKS_VERSION)
            handler.pointed.payload = this.stableRef.asCPointer()
        }.onFailure {
            memory.free()
        }.getOrThrow()
    }

    override val beforeFree: BeforeFree = {
        stableRef.dispose()
    }
}

class RemoteCallbacks(
    raw: RemoteCallbacksRaw = RemoteCallbacksRaw(),
) : GitBase<git_remote_callbacks, RemoteCallbacksRaw>(raw) {
    constructor(memory: Memory, handler: CPointer<git_remote_callbacks>) : this(RemoteCallbacksRaw(memory, handler))

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
                    val callback = payload!!.asStableRef<TransportMessageCallback>().get()
                    val result = callback.transportMessage(message!!.toKString())
                    if (result) 1 else 0
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
                    val callback = payload!!.asStableRef<RemoteCompletionCallback>().get()
                    callback.remoteCompletion(RemoteCompletionType.fromRaw(type))
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
                    val callback = payload!!.asStableRef<CredentialAcquireCallback>().get()
                    val credential = Credential(Memory(), cred!!.pointed.value!!)
                    callback.credentialAcquire(
                        credential,
                        url!!.toKString(),
                        usernameFromUrl!!.toKString(),
                        CredentialType(allowedTypes)
                    )
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
                    val callback = payload!!.asStableRef<CertificateCheckCallback>().get()
                    callback.certificateCheck(Cert(Memory(), cert!!), valid == 1, host!!.toKString())
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
                    val callback = payload!!.asStableRef<IndexerProgressCallback>().get()
                    val progress = IndexerProgress(
                        stats!!.pointed.total_objects,
                        stats.pointed.indexed_objects,
                        stats.pointed.received_objects,
                        stats.pointed.local_objects,
                        stats.pointed.total_deltas,
                        stats.pointed.indexed_deltas,
                        stats.pointed.received_bytes
                    )
                    callback.indexerProgress(progress)
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
                    val callback = payload!!.asStableRef<UpdateTipsCallback>().get()
                    callback.updateTips(refname!!.toKString(), Oid(Memory(), a!!), Oid(Memory(), b!!))
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
                    val callback = payload!!.asStableRef<PushTransferProgressCallback>().get()
                    callback.pushTransferProgress(current, total, bytes)
                }
            }
        }

    var pushUpdateReference: PushUpdateReferenceCallback? = null
        set(value) {
            field = value
            raw.handler.pointed.push_update_reference = value?.let {
                staticCFunction { refname, status, payload ->
                    val callback = payload!!.asStableRef<PushUpdateReferenceCallback>().get()
                    callback.pushUpdateReference(refname!!.toKString(), status!!.toKString()).value
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
                    val callback = payload!!.asStableRef<PushNegotiationCallback>().get()
                    val updateList = MutableList(size.toInt()) {
                        val update = updates!![it]!!.pointed
                        PushUpdate(
                            update.src_refname!!.toKString(),
                            update.dst_refname!!.toString(),
                            Oid(Memory(), update.src.ptr),
                            Oid(Memory(), update.dst.ptr)
                        )
                    }
                    callback.pushNegotiation(updateList)
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
                    val callback = payload!!.asStableRef<TransportCallback>().get()
                    callback.transport(
                        Transport(Memory(), transport!!.pointed.value!!),
                        Remote(Memory(), remote!!)
                    )
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
                    val callback = payload!!.asStableRef<RemoteReadyCallback>().get()
                    callback.remoteReady(Remote(Memory(), remote!!), Direction.fromRaw(direction.toUInt()))
                }
            }
        }
}
