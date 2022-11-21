package com.kgit2.signature

import com.kgit2.common.memory.Memory
import com.kgit2.memory.Binding
import com.kgit2.memory.GitBase
import com.kgit2.time.Time
import kotlinx.cinterop.*
import libgit2.git_signature
import libgit2.git_signature_free
import libgit2.git_signature_new
import libgit2.git_signature_now
import kotlin.native.internal.Cleaner
import kotlin.native.internal.createCleaner

typealias SignaturePointer = CPointer<git_signature>

typealias SignatureSecondaryPointer = CPointerVar<git_signature>

typealias SignatureInitial = SignatureSecondaryPointer.(Memory) -> Unit

class SignatureRaw(
    memory: Memory,
    handler: SignaturePointer,
) : Binding<git_signature>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: SignatureSecondaryPointer,
        initial: SignatureInitial? = null,
    ) : this(memory, handler.apply {
        runCatching {
            initial?.invoke(handler, memory)
        }.onFailure {
            git_signature_free(handler.value!!)
            memory.free()
        }.getOrThrow()
    }.value!!)

    override val beforeFree: () -> Unit = {
        git_signature_free(handler)
    }
}

class Signature(raw: SignatureRaw) : GitBase<git_signature, SignatureRaw>(raw) {
    constructor(memory: Memory, handler: CPointer<git_signature>) : this(SignatureRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: SignatureSecondaryPointer = memory.allocPointerTo(),
        initial: SignatureInitial? = null,
    ) : this(SignatureRaw(memory, handler, initial))

    constructor(
        name: String,
        email: String,
        time: Time? = null,
    ) : this(initial = {
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
