package com.kgit2.certificate

import kotlinx.cinterop.Arena
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.pointed
import kotlinx.cinterop.readBytes
import libgit2.git_cert_x509

class CertX509(
    parent: Cert,
    handler: CPointer<git_cert_x509>,
) : BaseCert<git_cert_x509, Cert>(parent, handler) {
    val data = handler.pointed.data?.readBytes(handler.pointed.len.toInt())
}
