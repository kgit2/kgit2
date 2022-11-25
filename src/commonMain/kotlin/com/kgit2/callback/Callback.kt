package com.kgit2.callback

import com.kgit2.callback.payload.CheckoutPerf
import com.kgit2.callback.payload.IndexerProgress
import com.kgit2.callback.payload.PushUpdate
import com.kgit2.certificate.Cert
import com.kgit2.checkout.CheckoutNotificationType
import com.kgit2.common.option.mutually.PackBuilderStage
import com.kgit2.credential.Credential
import com.kgit2.credential.CredentialType
import com.kgit2.diff.DiffFile
import com.kgit2.exception.GitErrorCode
import com.kgit2.fetch.Direction
import com.kgit2.model.Oid
import com.kgit2.remote.Remote
import com.kgit2.remote.RemoteCompletionType
import com.kgit2.repository.Repository
import com.kgit2.submodule.Submodule
import com.kgit2.transport.Transport
import com.kgit2.tree.TreeEntry

interface RepositoryCreateCallback {
    /**
     * The signature of a function matching git_repository_init, with an
     * additional void * as callback payload.
     *
     * Callers of git_clone my provide a function matching this signature
     * to override the repository creation and customization process
     * during a clone operation.
     *
     * @param repository the resulting repository
     * @param path path in which to create the repository
     * @param bare whether the repository is bare. This is the value from the clone options
     * @return 0, or a negative value to indicate error
     */
    fun repositoryCreate(repository: Repository, path: String, bare: Boolean): Int
}

interface RemoteCreateCallback {
    /**
     * The signature of a function matching git_remote_create, with an additional
     * void* as a callback payload.
     *
     * Callers of git_clone may provide a function matching this signature to override
     * the remote creation and customization process during a clone operation.
     *
     * @param remote the resulting remote
     * @param repository the repository in which to create the remote
     * @param name the remote's name
     * @param url the remote's url
     * @return GitErrorCode GIT_OK on success, GIT_EINVALIDSPEC, GIT_EEXISTS or an error code
     */
    fun remoteCreate(remote: Remote, repository: Repository, name: String, url: String): GitErrorCode
}

interface TransportMessageCallback {
    /**
     * Callback for messages received by the transport.
     *
     * Return a negative value to cancel the network operation.
     *
     * @param message The message from the transport
     */
    fun transportMessage(message: String): Boolean
}

interface IndexerProgressCallback {
    /**
     * Type for progress callbacks during indexing.  Return a value less
     * than zero to cancel the indexing or download.
     *
     * @param progress Structure containing information about the state of the transfer
     */
    fun indexerProgress(progress: IndexerProgress): Int
}

interface CheckoutProgressCallback {
    fun checkoutProgress(path: String, completedSteps: ULong, totalSteps: ULong)
}

interface CheckoutNotifyCallback {
    fun checkoutNotify(
        type: CheckoutNotificationType,
        path: String?,
        baseline: DiffFile?,
        target: DiffFile?,
        workdir: DiffFile?,
    ): GitErrorCode
}

interface CheckoutPerfCallback {
    fun checkoutPerf(data: CheckoutPerf)
}

interface RemoteCompletionCallback {
    /**
     * Completion is called when different parts of the download
     * process are done (currently unused).
     */
    fun remoteCompletion(type: RemoteCompletionType): Int
}

interface PushTransferProgressCallback {
    /**
     * Type definition for push transfer progress callbacks.
     *
     * This type is deprecated, but there is no plan to remove this
     * type definition at this time.
     */
    fun pushTransferProgress(current: UInt, total: UInt, bytes: ULong): Int
}

interface PushUpdateReferenceCallback {
    /**
     * Callback used to inform of the update status from the remote.
     *
     * Called for each updated reference on push. If `status` is
     * not `NULL`, the update was rejected by the remote server
     * and `status` contains the reason given.
     *
     * @param refname refname specifying to the remote ref
     * @param status status message sent from the remote
     * @return GIT_OK on success, otherwise an error
     */
    fun pushUpdateReference(refname: String, status: String): GitErrorCode
}

interface CredentialAcquireCallback {
    /**
     * Credential acquisition callback.
     *
     * This callback is usually involved any time another system might need
     * authentication. As such, you are expected to provide a valid
     * git_credential object back, depending on allowed_types (a
     * git_credential_t bitmask).
     *
     * Note that most authentication details are your responsibility - this
     * callback will be called until the authentication succeeds, or you report
     * an error. As such, it's easy to get in a loop if you fail to stop providing
     * the same incorrect credential.
     *
     * @param credential The newly created credential object.
     * @param url The resource for which we are demanding a credential.
     * @param usernameFromUrl The username that was embedded in a "user\@host"
     *                          remote url, or NULL if not included.
     * @param allowedTypes A bitmask stating which credential types are OK to return.
     * @return 0 for success, < 0 to indicate an error, > 0 to indicate
     *       no credential was acquired
     */
    fun credentialAcquire(
        credential: Credential,
        url: String,
        usernameFromUrl: String?,
        allowedTypes: CredentialType,
    ): Int
}

interface CertificateCheckCallback {
    /**
     * Callback for the user's custom certificate checks.
     *
     * @param cert The host certificate
     * @param valid Whether the libgit2 checks (OpenSSL or WinHTTP) think
     * this certificate is valid
     * @param host Hostname of the host libgit2 connected to
     * @return 0 to proceed with the connection, < 0 to fail the connection
     *         or > 0 to indicate that the callback refused to act and that
     *         the existing validity determination should be honored
     */
    fun certificateCheck(cert: Cert, valid: Boolean, host: String): Int
}

interface UpdateTipsCallback {
    /**
     * Callback for the user's custom update tips.
     *
     * @param refname The name of the reference that was updated
     * @param a The old OID for the reference
     * @param b The new OID for the reference
     * @return 0 to proceed with the update, < 0 to fail the update
     */
    fun updateTips(refname: String, a: Oid, b: Oid): Int
}

interface PackBuilderProgressCallback {
    /**
     * Callback for the user's custom packbuilder progress.
     *
     * @param stage The current stage of the packbuilder
     * @param current The current value of the stage
     * @param total The total value of the stage
     * @return 0 to proceed with the packbuilder, < 0 to fail the packbuilder
     */
    fun packBuilderProgress(stage: PackBuilderStage, current: ULong, total: ULong): Int
}

interface PushNegotiationCallback {
    /**
     * Callback for the user's custom push negotiation.
     *
     * @param updates The list of updates to be sent to the remote
     * @return 0 to proceed with the push, < 0 to fail the push
     */
    fun pushNegotiation(updates: List<PushUpdate>): Int
}

interface TransportCallback {
    /**
     * Callback for the user's custom transport.
     *
     * @param transport The transport to be used
     * @param remote The remote
     * @return 0 to proceed with the push, < 0 to fail the push
     */
    fun transport(transport: Transport, remote: Remote): Int
}

interface RemoteReadyCallback {
    /**
     * Callback for the user's custom remote ready.
     *
     * @param remote The remote to be used
     * @param direction GIT_DIRECTION_FETCH or GIT_DIRECTION_PUSH
     * @return 0 to proceed with the push, < 0 to fail the push
     */
    fun remoteReady(remote: Remote, direction: Direction): Int
}

interface SubmoduleCallback {
    /**
     * Function pointer to receive each submodule
     *
     * @param submodule git_submodule currently being visited
     * @param name name of the submodule
     * @return 0 on success or error code
     */
    fun submodule(submodule: Submodule, name: String): Int
}

interface TreeWalkCallback {
    /**
     * Function pointer to receive each entry in a tree
     *
     * @param root The root of the tree
     * @param entry The current entry
     * @return 0 on success or error code
     */
    fun treeWalk(root: String, entry: TreeEntry): Int
}
