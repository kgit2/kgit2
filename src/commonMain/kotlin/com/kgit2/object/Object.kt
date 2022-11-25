package com.kgit2.`object`

import cnames.structs.git_object
import com.kgit2.blob.Blob
import com.kgit2.commit.Commit
import com.kgit2.common.error.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.Raw
import com.kgit2.memory.GitBase
import com.kgit2.model.Oid
import com.kgit2.model.toKString
import com.kgit2.model.withGitBuf
import com.kgit2.tag.Tag
import com.kgit2.tree.Tree
import kotlinx.cinterop.*
import libgit2.*

typealias ObjectPointer = CPointer<git_object>

typealias ObjectSecondaryPointer = CPointerVar<git_object>

typealias ObjectInitial = ObjectSecondaryPointer.(Memory) -> Unit

class ObjectRaw(
    memory: Memory = Memory(),
    handler: ObjectPointer = memory.allocPointerTo<git_object>().value!!,
) : Raw<git_object>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: ObjectSecondaryPointer = memory.allocPointerTo<git_object>(),
        initial: ObjectInitial? = null,
    ) : this(memory, handler.apply {
        runCatching {
            initial?.invoke(handler, memory)
        }.onFailure {
            git_object_free(handler.value!!)
            memory.free()
        }.getOrThrow()
    }.value!!)

    override val beforeFree: () -> Unit = {
        git_object_free(handler)
    }
}

class Object(
    raw: ObjectRaw,
) : GitBase<git_object, ObjectRaw>(raw) {
    constructor(memory: Memory, handler: ObjectPointer) : this(ObjectRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: ObjectSecondaryPointer = memory.allocPointerTo(),
        initial: ObjectInitial? = null
    ) : this(ObjectRaw(memory, handler, initial))

    val oid: Oid = Oid(Memory(), git_object_id(raw.handler)!!)

    val shortId: String = withGitBuf { buf ->
        git_object_short_id(buf, raw.handler).errorCheck()
        buf.toKString()!!
    }

    val type: ObjectType = ObjectType.fromRaw(git_object_type(raw.handler))

    fun peel(targetType: ObjectType, memory: Memory = Memory()): Object {
        val obj = memory.allocPointerTo<git_object>()
        git_object_peel(obj.ptr, raw.handler, targetType.value).errorCheck()
        return Object(memory, obj.value!!)
    }

    fun peelToBlob(): Blob {
        val `object` = peel(ObjectType.Blob)
        `object`.raw.move()
        return Blob(`object`.raw.memory, `object`.raw.handler.reinterpret())
    }

    fun peelToCommit(): Commit {
        val `object` = peel(ObjectType.Commit)
        `object`.raw.move()
        return Commit(`object`.raw.memory, `object`.raw.handler.reinterpret())
    }

    fun peelToTag(): Tag {
        val `object` = peel(ObjectType.Tag)
        `object`.raw.move()
        return Tag(`object`.raw.memory, `object`.raw.handler.reinterpret())
    }

    fun peelToTree(): Tree {
        val `object` = peel(ObjectType.Tree)
        `object`.raw.move()
        return Tree(`object`.raw.memory, `object`.raw.handler.reinterpret())
    }
}
