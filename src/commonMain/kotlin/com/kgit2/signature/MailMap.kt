package com.kgit2.signature

import cnames.structs.git_mailmap
import com.kgit2.common.error.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.Raw
import com.kgit2.memory.GitBase
import kotlinx.cinterop.*
import libgit2.*
import kotlin.native.internal.Cleaner
import kotlin.native.internal.createCleaner

typealias MailMapPointer = CPointer<git_mailmap>

typealias MailMapSecondaryPointer = CPointerVar<git_mailmap>

typealias MailMapInitial = CPointerVar<git_mailmap>.(Memory) -> Unit

class MailMapRaw(
    memory: Memory,
    handler: MailMapPointer,
) : Raw<git_mailmap>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: MailMapSecondaryPointer = memory.allocPointerTo<git_mailmap>(),
        initial: MailMapInitial? = null,
    ) : this(memory, handler.apply {
        runCatching {
            initial?.invoke(handler, memory)
        }.onFailure {
            git_mailmap_free(handler.value!!)
            memory.free()
        }.getOrThrow()
    }.value!!)

    override fun free() {
        git_mailmap_free(handler)
    }
}

class MailMap(raw: MailMapRaw) : GitBase<git_mailmap, MailMapRaw>(raw) {
    constructor(memory: Memory, handler: MailMapPointer) : this(MailMapRaw(memory, handler))

    constructor(buf: String? = null) : this(
        MailMapRaw() { memory ->
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

    fun resolveSignature(signature: Signature): Signature = Signature() {
        git_mailmap_resolve_signature(ptr, raw.handler, signature.raw.handler).errorCheck()
    }
}
