package com.kgit2.signature

import com.kgit2.model.AutoFreeGitBase
import com.kgit2.time.Time
import kotlinx.cinterop.*
import libgit2.git_signature
import libgit2.git_signature_free
import libgit2.git_signature_new
import libgit2.git_signature_now

data class Signature(
    val name: String,
    val email: String,
    val time: Time?,
    override val handler: CPointer<git_signature>,
    override val arena: Arena,
) : AutoFreeGitBase<CPointer<git_signature>> {
    override fun free() {
        git_signature_free(handler)
        arena.clear()
    }

    companion object {
        fun new(name: String, email: String, time: Time? = null): Signature {
            val arena = Arena()
            val pointer = arena.allocPointerTo<git_signature>()
            when (time) {
                null -> git_signature_now(pointer.ptr, name, email)
                else -> git_signature_new(pointer.ptr, name, email, time.seconds, time.offset)
            }
            pointer.value!!
            return Signature(name, email, time, pointer.value!!, arena)
        }

        fun fromHandler(handler: CPointer<git_signature>, arena: Arena): Signature {
            val name = handler.pointed.name!!.toKString()
            val email = handler.pointed.email!!.toKString()
            val time = Time.fromHandler(handler.pointed.`when`.ptr)
            return Signature(name, email, time, handler, arena)
        }
    }
}
