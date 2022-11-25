package com.kgit2.certificate

import com.kgit2.common.memory.Memory
import com.kgit2.memory.Raw
import com.kgit2.memory.GitBase
import kotlinx.cinterop.*
import libgit2.git_cert

typealias CertPointer = CPointer<git_cert>

typealias CertSecondaryPointer = CPointerVar<git_cert>

typealias CertInitial = CertSecondaryPointer.(Memory) -> Unit

class CertRaw(
    memory: Memory,
    handler: CertPointer,
) : Raw<git_cert>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: CertSecondaryPointer = memory.allocPointerTo(),
        initial: CertInitial? = null,
    ) : this(memory, handler.apply {
        runCatching {
            initial?.invoke(handler, memory)
        }.onFailure {
            memory.free()
        }.getOrThrow()
    }.value!!)
}

class Cert(
    raw: CertRaw,
) : GitBase<git_cert, CertRaw>(raw) {
    constructor(memory: Memory, handler: CertPointer) : this(CertRaw(memory, handler))

    constructor(memory: Memory, handler: CertSecondaryPointer, initial: CertInitial?) : this(CertRaw(memory, handler.reinterpret(), initial))

    var certType: CertType = CertType.fromRaw(raw.handler.pointed.cert_type)
        set(value) {
            field = value
            raw.handler.pointed.cert_type = value.value
        }

    fun asHostKey(): CertHostKey? {
        return when (certType) {
            CertType.LIBSSH2 -> {
                raw.move()
                CertHostKey(raw.memory, raw.handler.reinterpret())
            }
            else -> null
        }
    }

    fun asX509(): CertX509? {
        return when (certType) {
            CertType.X509 -> {
                raw.move()
                CertX509(raw.memory, raw.handler.reinterpret())
            }
            else -> null
        }
    }
}
