package com.kgit2.certificate

import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import kotlinx.cinterop.pointed
import kotlinx.cinterop.reinterpret
import libgit2.git_cert

@Raw(
    base = git_cert::class,
)
class Cert(raw: CertRaw) : GitBase<git_cert, CertRaw>(raw) {
    constructor(
        memory: Memory,
        handler: CertPointer,
        initial: CertInitial? = null,
    ) : this(CertRaw(memory, handler, initial))

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
