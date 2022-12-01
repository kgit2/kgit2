package com.kgit2.signature

import cnames.structs.git_mailmap
import com.kgit2.annotations.Raw
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.RawWrapper
import kotlinx.cinterop.convert
import kotlinx.cinterop.ptr
import libgit2.git_mailmap_add_entry
import libgit2.git_mailmap_from_buffer
import libgit2.git_mailmap_new
import libgit2.git_mailmap_resolve_signature

@Raw(
    base = git_mailmap::class,
    free = "git_mailmap_free",
)
class MailMap(raw: MailmapRaw) : RawWrapper<git_mailmap, MailmapRaw>(raw) {
    constructor(memory: Memory, handler: MailmapPointer) : this(MailmapRaw(memory, handler))

    constructor(buf: String? = null) : this(MailmapRaw {
        when (buf) {
            null -> git_mailmap_new(ptr).errorCheck()
            else -> git_mailmap_from_buffer(ptr, buf, buf.length.convert())
        }
    })

    fun addEntry(realName: String, realEmail: String, replaceName: String, replaceEmail: String) {
        git_mailmap_add_entry(raw.handler, realName, realEmail, replaceName, replaceEmail).errorCheck()
    }

    fun resolveSignature(signature: Signature): Signature = Signature {
        git_mailmap_resolve_signature(ptr, raw.handler, signature.raw.handler).errorCheck()
    }
}
