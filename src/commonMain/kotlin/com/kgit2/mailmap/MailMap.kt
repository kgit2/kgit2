package com.kgit2.mailmap

import cnames.structs.git_mailmap
import com.kgit2.annotations.Raw
import com.kgit2.common.extend.errorCheck
import com.kgit2.memory.RawWrapper
import com.kgit2.repository.Repository
import com.kgit2.signature.Signature
import kotlinx.cinterop.convert
import kotlinx.cinterop.ptr
import kotlinx.cinterop.refTo
import kotlinx.cinterop.toKString
import libgit2.git_mailmap_add_entry
import libgit2.git_mailmap_from_buffer
import libgit2.git_mailmap_from_repository
import libgit2.git_mailmap_new
import libgit2.git_mailmap_resolve_signature

@Raw(
    base = git_mailmap::class,
    free = "git_mailmap_free",
)
class Mailmap(raw: MailmapRaw) : RawWrapper<git_mailmap, MailmapRaw>(raw) {
    constructor(buf: ByteArray? = null) : this(MailmapRaw {
        when (buf) {
            null -> git_mailmap_new(ptr).errorCheck()
            else -> git_mailmap_from_buffer(ptr, buf.refTo(0).getPointer(it).toKString(), buf.size.convert())
        }
    })

    constructor(repository: Repository) : this(MailmapRaw {
        git_mailmap_from_repository(this.ptr, repository.raw.handler)
    })

    fun addEntry(realName: String, realEmail: String, replaceName: String, replaceEmail: String) {
        git_mailmap_add_entry(raw.handler, realName, realEmail, replaceName, replaceEmail).errorCheck()
    }

    fun resolveSignature(signature: Signature): Signature =
        Signature { git_mailmap_resolve_signature(ptr, raw.handler, signature.raw.handler).errorCheck() }
}
