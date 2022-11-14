package com.kgit2.certificate

import com.kgit2.model.GitBase
import kotlinx.cinterop.*
import libgit2.git_cert
import libgit2.git_cert_hostkey
import libgit2.git_cert_x509

sealed class BaseCert<T : CPointed, B>(
    val parent: BaseCert<*, *>?,
    override val handler: CPointer<T>,
) : GitBase<CPointer<T>>

class Cert(
    handler: CPointer<git_cert>,
) : BaseCert<git_cert, Unit>(null, handler) {
    var certType: CertType = CertType.fromRaw(handler.pointed.cert_type)
        set(value) {
            field = value
            handler.pointed.cert_type = value.value
        }

    fun asHostKey(): CertHostKey? {
        return when (certType) {
            CertType.LIBSSH2 -> CertHostKey(this, handler.pointed.reinterpret<git_cert_hostkey>().ptr)
            else -> null
        }
    }

    fun asX509(): CertX509? {
        return when (certType) {
            CertType.X509 -> CertX509(this, handler.pointed.reinterpret<git_cert_x509>().ptr)
            else -> null
        }
    }
}
