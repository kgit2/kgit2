package com.kgit2.certificate

import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import kotlinx.cinterop.pointed
import kotlinx.cinterop.readBytes
import libgit2.git_cert_x509

@Raw(
    base = git_cert_x509::class,
)
class CertX509(raw: CertX509Raw) : GitBase<git_cert_x509, CertX509Raw>(raw) {
    constructor(
        memory: Memory,
        handler: CertX509Pointer,
        initial: CertX509Initial? = null,
    ) : this(CertX509Raw(memory, handler, initial))

    val data = raw.handler.pointed.data?.readBytes(raw.handler.pointed.len.toInt())
}
