package com.kgit2.certificate

import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import kotlinx.cinterop.pointed
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toKString
import libgit2.git_cert_hostkey

@Raw(
    base = "git_cert_hostkey",
)
class CertHostKey(raw: CertHostkeyRaw) : GitBase<git_cert_hostkey, CertHostkeyRaw>(raw) {
    constructor(memory: Memory, handler: CertHostkeyPointer) : this(CertHostkeyRaw(memory, handler))

    constructor(memory: Memory, handler: CertHostkeySecondaryPointer, initial: CertHostkeyInitial?) : this(
        CertHostkeyRaw(memory, handler.reinterpret(), initial)
    )

    val type = CertSSHType.fromRaw(raw.handler.pointed.type)

    val rawType = CertSSHRawType.fromRaw(raw.handler.pointed.raw_type)

    /**
     * Raw hostkey type. If `type` has `GIT_CERT_SSH_RAW` set, this will
     * have the type of the raw hostkey.
     */
    val hostKey = when (type) {
        CertSSHType.RAW -> raw.handler.pointed.hostkey?.toKString()
        else -> null
    }

    /**
     * Raw hostkey type. If `type` has `GIT_CERT_SSH_MD5` set, this will
     * have the type of the raw hostkey.
     */
    val hashMD5 = when (type) {
        CertSSHType.MD5 -> raw.handler.pointed.hash_md5.readBytes(16)
        else -> null
    }

    /**
     * Raw hostkey type. If `type` has `GIT_CERT_SSH_SHA1` set, this will
     * have the type of the raw hostkey.
     */
    val hashSHA1 = when (type) {
        CertSSHType.SHA1 -> raw.handler.pointed.hash_sha1.readBytes(20)
        else -> null
    }

    /**
     * Raw hostkey type. If `type` has `GIT_CERT_SSH_SHA256` set, this will
     * have the type of the raw hostkey.
     */
    val hashSHA256 = when (type) {
        CertSSHType.SHA256 -> raw.handler.pointed.hash_sha256.readBytes(32)
        else -> null
    }
}
