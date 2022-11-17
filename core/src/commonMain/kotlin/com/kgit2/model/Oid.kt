package com.kgit2.model

import com.kgit2.common.error.errorCheck
import com.kgit2.`object`.ObjectType
import kotlinx.cinterop.*
import libgit2.*

class Oid(
    override val handler: CPointer<git_oid>,
    override val arena: Arena,
) : AutoFreeGitBase<CPointer<git_oid>> {
    companion object {
        fun fromHexString(hexString: String): Oid {
            val arena = Arena()
            val gitOid = arena.alloc<git_oid>()
            runCatching {
                git_oid_fromstrn(gitOid.ptr, hexString, hexString.length.convert()).errorCheck()
            }.onFailure {
                arena.clear()
                throw it
            }
            return Oid(gitOid.ptr, arena)
        }

        fun hashObject(kind: ObjectType, data: ByteArray): Oid {
            val arena = Arena()
            val gitOid = arena.alloc<git_oid>()
            git_odb_hash(gitOid.ptr, data.refTo(0), data.size.convert(), kind.value).errorCheck()
            return Oid(gitOid.ptr, arena)
        }

        fun hashFile(path: String, kind: ObjectType): Oid {
            val arena = Arena()
            val gitOid = arena.alloc<git_oid>()
            git_odb_hashfile(gitOid.ptr, path, kind.value).errorCheck()
            return Oid(gitOid.ptr, arena)
        }

        fun zero(): Oid {
            val arena = Arena()
            val gitOid = arena.alloc<git_oid>()
            git_oid_fromstrn(gitOid.ptr, "0000000000000000000000000000000000000000", 40.convert()).errorCheck()
            return Oid(gitOid.ptr, arena)
        }
    }

    fun isZero(): Boolean {
        return git_oid_is_zero(handler) == 1
    }

    fun copy(): Oid {
        val arena = Arena()
        val gitOid = arena.alloc<git_oid>()
        git_oid_cpy(gitOid.ptr, handler).errorCheck()
        return Oid(gitOid.ptr, arena)
    }

    operator fun compareTo(other: Oid): Int {
        return git_oid_cmp(handler, other.handler)
    }

    operator fun compareTo(other: String): Int {
        return git_oid_strcmp(handler, other)
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Oid -> git_oid_equal(handler, other.handler) != 0
            is String -> git_oid_streq(handler, other) == 0
            else -> false
        }
    }

    override fun toString(): String {
        val buffer = ByteArray(GIT_OID_HEXSZ + 1)
        git_oid_fmt(buffer.refTo(0), handler).errorCheck()
        return buffer.toKString()
    }

    override fun hashCode(): Int {
        return toString().hashCode()
    }
}
