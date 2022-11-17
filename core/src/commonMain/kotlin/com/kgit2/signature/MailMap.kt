package com.kgit2.signature

import cnames.structs.git_mailmap
import com.kgit2.common.error.errorCheck
import com.kgit2.model.AutoFreeGitBase
import kotlinx.cinterop.*
import libgit2.*

class MailMap(
    override val handler: CPointer<git_mailmap>,
    override val arena: Arena,
) : AutoFreeGitBase<CPointer<git_mailmap>> {
    companion object {
        fun new(buf: String? = null): MailMap {
            val arena = Arena()
            val pointer = arena.allocPointerTo<git_mailmap>()
            when (buf) {
                null -> git_mailmap_new(pointer.ptr).errorCheck()
                else -> git_mailmap_from_buffer(pointer.ptr, buf, buf.length.convert())
            }
            return fromHandler( pointer.value!!, arena)
        }

        fun fromHandler(handler: CPointer<git_mailmap>, arena: Arena): MailMap {
            return MailMap(handler, arena)
        }
    }

    override fun free() {
        git_mailmap_free(handler)
        arena.clear()
    }

    fun addEntry(realName: String, realEmail: String, replaceName: String, replaceEmail: String) {
        git_mailmap_add_entry(handler, realName, realEmail, replaceName, replaceEmail).errorCheck()
    }

    fun resolveSignature(signature: Signature): Signature {
        val arena = Arena()
        val out = arena.allocPointerTo<git_signature>()
        git_mailmap_resolve_signature(out.ptr, handler, signature.handler).errorCheck()
        return Signature.fromHandler(out.value!!, arena)
    }
}
