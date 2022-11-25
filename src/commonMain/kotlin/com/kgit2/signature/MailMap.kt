package com.kgit2.signature

import cnames.structs.git_mailmap
import com.kgit2.annotations.Raw
import com.kgit2.common.error.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import kotlinx.cinterop.convert
import kotlinx.cinterop.ptr
import libgit2.git_mailmap_add_entry
import libgit2.git_mailmap_from_buffer
import libgit2.git_mailmap_new
import libgit2.git_mailmap_resolve_signature
import kotlin.native.internal.Cleaner
import kotlin.native.internal.createCleaner

@Raw(
    base = "git_mailmap",
    free = "git_mailmap_free",
)
class MailMap(raw: MailmapRaw) : GitBase<git_mailmap, MailmapRaw>(raw) {
    constructor(memory: Memory, handler: MailmapPointer) : this(MailmapRaw(memory, handler))

    constructor(buf: String? = null) : this(
        MailmapRaw { memory ->
            runCatching {
                when (buf) {
                    null -> git_mailmap_new(ptr).errorCheck()
                    else -> git_mailmap_from_buffer(ptr, buf, buf.length.convert())
                }
            }.onFailure {
                memory.free()
            }.getOrThrow()
        }
    )

    override val cleaner: Cleaner = createCleaner(raw) { raw.free() }

    fun addEntry(realName: String, realEmail: String, replaceName: String, replaceEmail: String) {
        git_mailmap_add_entry(raw.handler, realName, realEmail, replaceName, replaceEmail).errorCheck()
    }

    fun resolveSignature(signature: Signature): Signature = Signature {
        git_mailmap_resolve_signature(ptr, raw.handler, signature.raw.handler).errorCheck()
    }
}
