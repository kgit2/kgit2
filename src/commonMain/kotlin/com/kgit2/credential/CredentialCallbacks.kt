package com.kgit2.credential

import com.kgit2.certificate.Cert
import com.kgit2.common.error.GitErrorCode
import com.kgit2.common.extend.toBoolean
import com.kgit2.common.memory.Memory
import kotlinx.cinterop.*
import libgit2.git_cert
import libgit2.git_credential
import libgit2.git_credential_acquire_cb
import libgit2.git_transport_certificate_check_cb

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
typealias CredentialAcquireCallback = (credential: Credential, url: String, usernameFromUrl: String?, allowedTypes: CredentialType) -> GitErrorCode

interface CredentialAcquireCallbackPayload {
    var credentialAcquireCallback: CredentialAcquireCallback?
}

val staticCredentialAcquireCallback: git_credential_acquire_cb = staticCFunction {
        credential: CPointer<CPointerVar<git_credential>>?,
        url: CPointer<ByteVar>?,
        usernameFromUrl: CPointer<ByteVar>?,
        allowedTypes: UInt,
        payload: COpaquePointer?
    ->
    val callback = payload!!.asStableRef<CredentialAcquireCallbackPayload>().get()
    callback.credentialAcquireCallback!!.invoke(
        Credential(Memory(), credential!!.pointed.value!!),
        url!!.toKString(),
        usernameFromUrl!!.toKString(),
        CredentialType(allowedTypes)
    ).value
}

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
typealias CertificateCheckCallback = (cert: Cert, valid: Boolean, host: String) -> GitErrorCode

interface CertificateCheckCallbackPayload {
    var certificateCheckCallback: CertificateCheckCallback?
}

val staticCertificateCheckCallback: git_transport_certificate_check_cb = staticCFunction {
        cert: CPointer<git_cert>?,
        valid: Int,
        host: CPointer<ByteVar>?,
        payload: COpaquePointer?,
    ->
    val callback = payload!!.asStableRef<CertificateCheckCallbackPayload>().get()
    callback.certificateCheckCallback!!.invoke(Cert(Memory(), cert!!), valid.toBoolean(), host!!.toKString()).value
}
