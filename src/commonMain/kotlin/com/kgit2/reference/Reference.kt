package com.kgit2.reference

import cnames.structs.git_reference
import com.kgit2.annotations.Raw
import com.kgit2.blob.Blob
import com.kgit2.commit.Commit
import com.kgit2.common.error.errorCheck
import com.kgit2.common.error.toBoolean
import com.kgit2.common.error.toInt
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import com.kgit2.model.Oid
import com.kgit2.`object`.Object
import com.kgit2.`object`.ObjectType
import com.kgit2.tag.Tag
import com.kgit2.tree.Tree
import kotlinx.cinterop.*
import libgit2.*

@Raw(
    base = "git_reference",
    free = "git_reference_free",
)
class Reference(raw: ReferenceRaw) : GitBase<git_reference, ReferenceRaw>(raw) {
    constructor(memory: Memory, handler: ReferencePointer) : this(ReferenceRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: ReferenceSecondaryPointer = memory.allocPointerTo(),
        initial: ReferenceInitial? = null,
    ) : this(ReferenceRaw(memory, handler, initial))

    companion object {
        fun isValidName(name: String): Boolean = git_reference_is_valid_name(name).toBoolean()

        fun normalizeName(name: String, flags: ReferenceFormat): String = memScoped {
            val buf = allocPointerTo<ByteVar>()
            val size = alloc<ULongVar>()
            git_reference_normalize_name(buf.value, size.value, name, flags.value).errorCheck()
            buf.value!!.readBytes(size.value.toInt()).toKString()
        }
    }

    val name: String = git_reference_name(raw.handler)!!.toKString()

    val shorthand: String = git_reference_shorthand(raw.handler)!!.toKString()

    val isBranch: Boolean = git_reference_is_branch(raw.handler).toBoolean()

    val isRemote: Boolean = git_reference_is_remote(raw.handler).toBoolean()

    val isTag: Boolean = git_reference_is_tag(raw.handler).toBoolean()

    val isNote: Boolean = git_reference_is_note(raw.handler).toBoolean()

    val type: ReferenceType = ReferenceType.fromRaw(git_reference_type(raw.handler))

    val target: Oid? = git_reference_target(raw.handler)?.let { Oid(Memory(), it) }

    val targetPeel: Oid? = git_reference_target_peel(raw.handler)?.let { Oid(Memory(), it) }

    val symbolicTarget: String? = git_reference_symbolic_target(raw.handler)?.toKString()

    fun resolve(): Reference = Reference {
        git_reference_resolve(this.ptr, raw.handler).errorCheck()
    }

    fun peel(targetType: ObjectType): Object = Object {
        git_reference_peel(this.ptr, raw.handler, targetType.value).errorCheck()
    }

    fun peelToBlob(): Blob {
        val `object` = peel(ObjectType.Blob)
        `object`.raw.move()
        return Blob(`object`.raw.memory, `object`.raw.handler.reinterpret())
    }

    fun peelToCommit(): Commit {
        val `object` = peel(ObjectType.Blob)
        `object`.raw.move()
        return Commit(`object`.raw.memory, `object`.raw.handler.reinterpret())
    }

    fun peelToTree(): Tree {
        val `object` = peel(ObjectType.Blob)
        `object`.raw.move()
        return Tree(`object`.raw.memory, `object`.raw.handler.reinterpret())
    }

    fun peelToTag(): Tag {
        val `object` = peel(ObjectType.Blob)
        `object`.raw.move()
        return Tag(`object`.raw.memory, `object`.raw.handler.reinterpret())
    }

    /**
     * @param newName The new name for the reference
     * @param force Overwrite an existing reference
     * @param logMessage The one line long message to be appended to the reflog
     */
    fun rename(newName: String, force: Boolean = false, logMessage: String? = null): Reference = Reference {
        git_reference_rename(this.ptr, raw.handler, newName, force.toInt(), logMessage).errorCheck()
    }

    fun setTarget(oid: Oid, logMessage: String? = null): Reference = Reference {
        git_reference_set_target(this.ptr, raw.handler, oid.raw.handler, logMessage).errorCheck()
    }

    fun delete() {
        git_reference_delete(raw.handler).errorCheck()
        this.raw.free()
    }
}
