package com.kgit2.certificate

import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import kotlinx.cinterop.pointed
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.reinterpret
import libgit2.git_cert_x509

@Raw(
    base = "git_cert_x509",
)
class CertX509(raw: CertX509Raw) : GitBase<git_cert_x509, CertX509Raw>(raw) {
    constructor(memory: Memory, handler: CertX509Pointer) : this(CertX509Raw(memory, handler))

    constructor(memory: Memory, handler: CertX509SecondaryPointer, initial: CertX509Initial?) : this(
        CertX509Raw(
            memory,
            handler.reinterpret(),
            initial
        )
    )

    val data = raw.handler.pointed.data?.readBytes(raw.handler.pointed.len.toInt())
}
