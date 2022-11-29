package com.kgit2.credential

import com.kgit2.certificate.Cert
import com.kgit2.common.error.GitErrorCode

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
