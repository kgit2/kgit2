package com.kgit2.signature

import com.kgit2.annotations.Raw
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.RawWrapper
import com.kgit2.time.Time
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import libgit2.git_signature
import libgit2.git_signature_new
import libgit2.git_signature_now

@Raw(
    base = git_signature::class,
    free = "git_signature_free",
)
class Signature(raw: SignatureRaw) : RawWrapper<git_signature, SignatureRaw>(raw) {
    constructor(memory: Memory = Memory(), handler: CPointer<git_signature>) : this(SignatureRaw(memory, handler)) {
        raw.beforeFree = null;
    }

    constructor(
        memory: Memory = Memory(),
        secondary: SignatureSecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: SignatureSecondaryInitial? = null,
    ) : this(SignatureRaw(memory, secondary, secondaryInitial))

    constructor(
        name: String,
        email: String,
        time: Time? = null,
    ) : this(SignatureRaw(secondaryInitial = {
        when (time) {
            null -> git_signature_now(this.ptr, name, email)
            else -> git_signature_new(this.ptr, name, email, time.seconds, time.offset)
        }.errorCheck()
    }))

    val name: String = raw.handler.pointed.name!!.toKString()

    val email: String = raw.handler.pointed.email!!.toKString()

    val time: Time = raw.handler.pointed.`when`.let { Time(raw.memory, it) }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Signature) return false

        if (name != other.name) return false
        if (email != other.email) return false
        if (time != other.time) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + time.hashCode()
        return result
    }

    override fun toString(): String {
        return "Signature(name='$name', email='$email', time=$time)"
    }

}
