package com.kgit2.certificate

import libgit2.*

enum class CertType(val value: git_cert_t) {
    /**
     * No information about the certificate is available. This may
     * happen when using curl.
     */
    None(git_cert_t.GIT_CERT_NONE),

    /**
     * The `data` argument to the callback will be a pointer to
     * the DER-encoded data.
     */
    X509(git_cert_t.GIT_CERT_X509),

    /**
     * The `data` argument to the callback will be a pointer to a
     * `git_cert_hostkey` structure.
     */
    LIBSSH2(git_cert_t.GIT_CERT_HOSTKEY_LIBSSH2),

    /**
     * The `data` argument to the callback will be a pointer to a
     * `git_strarray` with `name:content` strings containing
     * information about the certificate. This is used when using
     * curl.
     */
    StrArray(git_cert_t.GIT_CERT_STRARRAY);

    fun toRaw(): git_cert_t {
        return value
    }

    companion object {
        fun fromRaw(value: git_cert_t): CertType {
            return when (value) {
                git_cert_t.GIT_CERT_NONE -> None
                git_cert_t.GIT_CERT_X509 -> X509
                git_cert_t.GIT_CERT_HOSTKEY_LIBSSH2 -> LIBSSH2
                git_cert_t.GIT_CERT_STRARRAY -> StrArray
                else -> error("Unknown value: $value")
            }
        }
    }
}

enum class CertSSHType(val value: git_cert_ssh_t) {
    /** MD5 is available */
    MD5(GIT_CERT_SSH_MD5),

    /** SHA-1 is available */
    SHA1(GIT_CERT_SSH_SHA1),

    /** SHA-256 is available */
    SHA256(GIT_CERT_SSH_SHA256),

    /** Raw hostkey is available */
    RAW(GIT_CERT_SSH_RAW);

    fun toRaw(): git_cert_ssh_t {
        return value
    }

    companion object {
        fun fromRaw(value: git_cert_ssh_t): CertSSHType {
            return when (value) {
                GIT_CERT_SSH_MD5 -> MD5
                GIT_CERT_SSH_SHA1 -> SHA1
                GIT_CERT_SSH_SHA256 -> SHA256
                GIT_CERT_SSH_RAW -> RAW
                else -> error("Unknown value: $value")
            }
        }
    }
}

enum class CertSSHRawType(val value: git_cert_ssh_raw_type_t) {
    /** RSA hostkey */
    Unknown(GIT_CERT_SSH_RAW_TYPE_UNKNOWN),

    /** RSA hostkey */
    RSA(GIT_CERT_SSH_RAW_TYPE_RSA),

    /** DSA hostkey */
    DSS(GIT_CERT_SSH_RAW_TYPE_DSS),

    KEY_ECDSA_256(GIT_CERT_SSH_RAW_TYPE_KEY_ECDSA_256),

    KEY_ECDSA_384(GIT_CERT_SSH_RAW_TYPE_KEY_ECDSA_384),

    KEY_ECDSA_521(GIT_CERT_SSH_RAW_TYPE_KEY_ECDSA_521),

    KEY_ED25519(GIT_CERT_SSH_RAW_TYPE_KEY_ED25519);

    fun toRaw(): git_cert_ssh_raw_type_t {
        return value
    }

    companion object {
        fun fromRaw(value: git_cert_ssh_raw_type_t): CertSSHRawType {
            return when (value) {
                GIT_CERT_SSH_RAW_TYPE_UNKNOWN -> Unknown
                GIT_CERT_SSH_RAW_TYPE_RSA -> RSA
                GIT_CERT_SSH_RAW_TYPE_DSS -> DSS
                GIT_CERT_SSH_RAW_TYPE_KEY_ECDSA_256 -> KEY_ECDSA_256
                GIT_CERT_SSH_RAW_TYPE_KEY_ECDSA_384 -> KEY_ECDSA_384
                GIT_CERT_SSH_RAW_TYPE_KEY_ECDSA_521 -> KEY_ECDSA_521
                GIT_CERT_SSH_RAW_TYPE_KEY_ED25519 -> KEY_ED25519
                else -> error("Unknown value: $value")
            }
        }
    }
}
