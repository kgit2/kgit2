package com.kgit2.certificate

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.pointed
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.toKString
import libgit2.git_cert_hostkey

class CertHostKey(
    parent: Cert,
    handler: CPointer<git_cert_hostkey>,
) : BaseCert<git_cert_hostkey, Cert>(parent, handler) {
    val type = CertSSHType.fromRaw(handler.pointed.type)

    val rawType = CertSSHRawType.fromRaw(handler.pointed.raw_type)

    /**
     * Raw hostkey type. If `type` has `GIT_CERT_SSH_RAW` set, this will
     * have the type of the raw hostkey.
     */
    val hostKey = when (type) {
        CertSSHType.RAW -> handler.pointed.hostkey?.toKString()
        else -> null
    }

    /**
     * Raw hostkey type. If `type` has `GIT_CERT_SSH_MD5` set, this will
     * have the type of the raw hostkey.
     */
    val hashMD5 = when (type) {
        CertSSHType.MD5 -> handler.pointed.hash_md5.readBytes(16)
        else -> null
    }

    /**
     * Raw hostkey type. If `type` has `GIT_CERT_SSH_SHA1` set, this will
     * have the type of the raw hostkey.
     */
    val hashSHA1 = when (type) {
        CertSSHType.SHA1 -> handler.pointed.hash_sha1.readBytes(20)
        else -> null
    }

    /**
     * Raw hostkey type. If `type` has `GIT_CERT_SSH_SHA256` set, this will
     * have the type of the raw hostkey.
     */
    val hashSHA256 = when (type) {
        CertSSHType.SHA256 -> handler.pointed.hash_sha256.readBytes(32)
        else -> null
    }
}
