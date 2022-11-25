package com.kgit2.certificate

import com.kgit2.common.memory.Memory
import com.kgit2.memory.Raw
import com.kgit2.memory.GitBase
import kotlinx.cinterop.*
import libgit2.git_cert_x509

typealias CertX509Pointer = CPointer<git_cert_x509>

typealias CertX509SecondaryPointer = CPointerVar<git_cert_x509>

typealias CertX509Initial = CertX509SecondaryPointer.(Memory) -> Unit

class CertX509Raw(
    memory: Memory,
    handler: CertX509Pointer,
) : Raw<git_cert_x509>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: CertX509SecondaryPointer = memory.allocPointerTo(),
        initial: CertX509Initial? = null,
    ) : this(memory, handler.apply {
        runCatching {
            initial?.invoke(handler, memory)
        }.onFailure {
            memory.free()
        }.getOrThrow()
    }.value!!)
}

class CertX509(
    raw: CertX509Raw,
) : GitBase<git_cert_x509, CertX509Raw>(raw) {
    constructor(memory: Memory, handler: CertX509Pointer) : this(CertX509Raw(memory, handler))

    constructor(memory: Memory, handler: CertX509SecondaryPointer, initial: CertX509Initial?) : this(CertX509Raw(memory, handler.reinterpret(), initial))

    val data = raw.handler.pointed.data?.readBytes(raw.handler.pointed.len.toInt())
}
