package com.kgit2.certificate

import com.kgit2.common.memory.Memory
import com.kgit2.memory.Binding
import com.kgit2.memory.GitBase
import kotlinx.cinterop.*
import libgit2.git_cert_hostkey

typealias CertHostKeyPointer = CPointer<git_cert_hostkey>

typealias CertHostKeySecondaryPointer = CPointerVar<git_cert_hostkey>

typealias CertHostKeyInitial = CertHostKeySecondaryPointer.(Memory) -> Unit

class CertHostKeyRaw(
    memory: Memory,
    handler: CertHostKeyPointer,
) : Binding<git_cert_hostkey>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: CertHostKeySecondaryPointer = memory.allocPointerTo(),
        initial: CertHostKeyInitial? = null,
    ) : this(memory, handler.apply {
        runCatching {
            initial?.invoke(handler, memory)
        }.onFailure {
            memory.free()
        }.getOrThrow()
    }.value!!)
}

class CertHostKey(
    raw: CertHostKeyRaw,
) : GitBase<git_cert_hostkey, CertHostKeyRaw>(raw) {
    constructor(memory: Memory, handler: CertHostKeyPointer) : this(CertHostKeyRaw(memory, handler))

    constructor(memory: Memory, handler: CertHostKeySecondaryPointer, initial: CertHostKeyInitial?) : this(CertHostKeyRaw(memory, handler.reinterpret(), initial))

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
