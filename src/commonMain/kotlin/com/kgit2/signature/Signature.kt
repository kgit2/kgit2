package com.kgit2.signature

import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import com.kgit2.time.Time
import kotlinx.cinterop.*
import libgit2.git_signature
import libgit2.git_signature_new
import libgit2.git_signature_now
import kotlin.native.internal.Cleaner
import kotlin.native.internal.createCleaner

@Raw(
    base = git_signature::class,
    free = "git_signature_free",
)
class Signature(raw: SignatureRaw) : GitBase<git_signature, SignatureRaw>(raw) {
    constructor(memory: Memory = Memory(), handler: CPointer<git_signature>) : this(SignatureRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        secondary: SignatureSecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: SignatureSecondaryInitial? = null,
    ) : this(SignatureRaw(memory, secondary, secondaryInitial))

    constructor(
        name: String,
        email: String,
        time: Time? = null,
    ) : this(secondaryInitial = {
        when (time) {
            null -> git_signature_now(ptr, name, email)
            else -> git_signature_new(ptr, name, email, time.seconds, time.offset)
        }
    })

    val name: String = raw.handler.pointed.name!!.toKString()

    val email: String = raw.handler.pointed.email!!.toKString()

    val time: Time = raw.handler.pointed.`when`.let { Time(raw.memory, it) }

    override val cleaner: Cleaner = createCleaner(raw) { it.free() }
}
