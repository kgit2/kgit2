package com.kgit2.model

import com.kgit2.common.error.errorCheck
import com.kgit2.common.error.toBoolean
import com.kgit2.common.memory.Memory
import com.kgit2.memory.Raw
import com.kgit2.memory.GitBase
import com.kgit2.`object`.ObjectType
import kotlinx.cinterop.*
import libgit2.*

typealias OidPointer = CPointer<git_oid>

typealias OidSecondaryPointer = CPointerVar<git_oid>

typealias OidInitial = OidPointer.(Memory) -> Unit

class OidRaw(
    memory: Memory = Memory(),
    handler: OidPointer = memory.alloc<git_oid>().ptr,
    initial: OidInitial? = null,
) : Raw<git_oid>(memory, handler.apply {
    runCatching {
        initial?.invoke(handler, memory)
    }.onFailure {
        memory.free()
    }.getOrThrow()
})

class Oid(
    raw: OidRaw,
) : GitBase<git_oid, OidRaw>(raw) {
    constructor(memory: Memory, handler: OidPointer) : this(OidRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: OidPointer = memory.alloc<git_oid>().ptr,
        initial: OidInitial?,
    ) : this(OidRaw(memory, handler, initial))

    constructor(hexString: String) : this(initial = {
        git_oid_fromstrn(
            this,
            hexString,
            hexString.length.convert()
        ).errorCheck()
    })

    constructor(kind: ObjectType, hashData: ByteArray) : this(initial = {
        git_odb_hash(
            this,
            hashData.refTo(0),
            hashData.size.convert(),
            kind.value
        ).errorCheck()
    })

    constructor(kind: ObjectType, hashFilePath: String) : this(initial = {
        git_odb_hashfile(
            this,
            hashFilePath,
            kind.value
        ).errorCheck()
    })

    companion object {
        val zero: Oid
            get() = Oid("0000000000000000000000000000000000000000")
    }

    fun isZero(): Boolean {
        return git_oid_is_zero(raw.handler).toBoolean()
    }

    fun copy(): Oid = Oid(initial = { git_oid_cpy(this, raw.handler).errorCheck() })

    operator fun compareTo(other: Oid): Int {
        return git_oid_cmp(raw.handler, other.raw.handler)
    }

    operator fun compareTo(other: String): Int {
        return git_oid_strcmp(raw.handler, other)
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Oid -> git_oid_equal(raw.handler, other.raw.handler) != 0
            is String -> git_oid_streq(raw.handler, other) == 0
            else -> false
        }
    }

    override fun toString(): String {
        val buffer = ByteArray(GIT_OID_HEXSZ + 1)
        git_oid_fmt(buffer.refTo(0), raw.handler).errorCheck()
        return buffer.toKString()
    }

    override fun hashCode(): Int {
        return toString().hashCode()
    }
}
