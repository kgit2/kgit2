package com.kgit2.reference

import com.kgit2.blob.Blob
import com.kgit2.commit.Commit
import com.kgit2.common.error.errorCheck
import com.kgit2.common.error.toBoolean
import com.kgit2.common.error.toInt
import com.kgit2.model.AutoFreeGitBase
import com.kgit2.model.Oid
import com.kgit2.`object`.Object
import com.kgit2.`object`.ObjectType
import com.kgit2.tag.Tag
import com.kgit2.tree.Tree
import kotlinx.cinterop.*
import libgit2.*

class Reference(
    override val handler: CPointer<git_reference>,
    override val arena: Arena,
) : AutoFreeGitBase<CPointer<git_reference>> {
    companion object {
        fun isValidName(name: String): Boolean = git_reference_is_valid_name(name).toBoolean()

        fun normalizeName(name: String, flags: ReferenceFormat): String = memScoped {
            val buf = allocPointerTo<ByteVar>()
            val size = alloc<ULongVar>()
            git_reference_normalize_name(buf.value, size.value, name, flags.value).errorCheck()
            buf.value!!.readBytes(size.value.toInt()).toKString()
        }
    }

    override fun free() {
        git_reference_free(handler)
        super.free()
    }

    val name: String = git_reference_name(handler)!!.toKString()

    val shorthand: String = git_reference_shorthand(handler)!!.toKString()

    val isBranch: Boolean = git_reference_is_branch(handler).toBoolean()

    val isRemote: Boolean = git_reference_is_remote(handler).toBoolean()

    val isTag: Boolean = git_reference_is_tag(handler).toBoolean()

    val isNote: Boolean = git_reference_is_note(handler).toBoolean()

    val type: ReferenceType = ReferenceType.fromRaw(git_reference_type(handler))

    val target: Oid? = git_reference_target(handler)?.let { Oid(it, arena) }

    val targetPeel: Oid? = git_reference_target_peel(handler)?.let { Oid(it, arena) }

    val symbolicTarget: String? = git_reference_symbolic_target(handler)?.toKString()

    fun resolve(): Reference {
        val arena = Arena()
        val handler = arena.allocPointerTo<git_reference>()
        git_reference_resolve(handler.ptr, this.handler).errorCheck()
        return Reference(handler.value!!, arena)
    }

    fun peel(targetType: ObjectType): Object {
        val arena = Arena()
        val handler = arena.allocPointerTo<git_object>()
        git_reference_peel(handler.ptr, this.handler, targetType.value).errorCheck()
        return Object(handler.value!!, arena)
    }

    fun peelToBlob(): Blob {
        val blobObject = peel(ObjectType.Blob)
        return Blob(blobObject.handler.reinterpret(), blobObject.arena)
    }

    fun peelToCommit(): Commit {
        val commitObject = peel(ObjectType.Commit)
        return Commit(commitObject.handler.reinterpret(), commitObject.arena)
    }

    fun peelToTree(): Tree {
        val treeObject = peel(ObjectType.Tree)
        return Tree(treeObject.handler.reinterpret(), treeObject.arena)
    }

    fun peelToTag(): Tag {
        val tagObject = peel(ObjectType.Tag)
        return Tag(tagObject.handler.reinterpret(), tagObject.arena)
    }

    /**
     * @param newName The new name for the reference
     * @param force Overwrite an existing reference
     * @param logMessage The one line long message to be appended to the reflog
     */
    fun rename(newName: String, force: Boolean = false, logMessage: String? = null): Reference {
        val arena = Arena()
        val handler = arena.allocPointerTo<git_reference>()
        git_reference_rename(handler.ptr, this.handler, newName, force.toInt(), logMessage).errorCheck()
        return Reference(handler.value!!, arena)
    }

    fun setTarget(oid: Oid, logMessage: String? = null): Reference {
        val arena = Arena()
        val handler = arena.allocPointerTo<git_reference>()
        git_reference_set_target(handler.ptr, this.handler, oid.handler, logMessage).errorCheck()
        return Reference(handler.value!!, arena)
    }

    fun delete() {
        git_reference_delete(handler).errorCheck()
        this.free()
    }
}
