package com.kgit2.`object`

import cnames.structs.git_blob
import cnames.structs.git_commit
import cnames.structs.git_object
import cnames.structs.git_tag
import cnames.structs.git_tree
import com.kgit2.common.error.errorCheck
import com.kgit2.model.AutoFreeGitBase
import com.kgit2.model.Oid
import com.kgit2.model.toKString
import com.kgit2.model.withGitBuf
import kotlinx.cinterop.*
import libgit2.*
import kotlin.String
import kotlin.TODO
import kotlin.Unit

sealed class ObjectBase<T : CPointed>(
    override val handler: CPointer<T>,
    override val arena: Arena,
) : AutoFreeGitBase<CPointer<T>>

class Object(
    handler: CPointer<git_object>,
    arena: Arena,
) : ObjectBase<git_object>(handler, arena) {
    override fun free() {
        git_object_free(handler)
        super.free()
    }

    val oid: Oid = Oid(git_object_id(handler)!!, arena)

    val shortId: String = withGitBuf { buf ->
        git_object_short_id(buf, handler).errorCheck()
        buf.toKString()!!
    }

    val type: ObjectType = ObjectType.fromRaw(git_object_type(handler))

    fun peel(targetType: ObjectType): Object {
        val arena = Arena()
        val obj = arena.allocPointerTo<git_object>()
        git_object_peel(obj.ptr, handler, targetType.value).errorCheck()
        return Object(obj.value!!, arena)
    }

    fun peelToBlob() {
        val arena = Arena()
        val obj = arena.allocPointerTo<git_object>()
        git_object_peel(obj.ptr, handler, ObjectType.Blob.value).errorCheck()
        val target = obj.ptr.reinterpret<git_blob>()
        TODO("Not yet implemented")
    }

    fun peelToCommit() {
        val arena = Arena()
        val obj = arena.allocPointerTo<git_object>()
        git_object_peel(obj.ptr, handler, ObjectType.Commit.value).errorCheck()
        val target = obj.ptr.reinterpret<git_commit>()
        TODO("Not yet implemented")
    }

    fun peelToTag() {
        val arena = Arena()
        val obj = arena.allocPointerTo<git_object>()
        git_object_peel(obj.ptr, handler, ObjectType.Tag.value).errorCheck()
        val target = obj.ptr.reinterpret<git_tag>()
        TODO("Not yet implemented")
    }

    fun peelToTree() {
        val arena = Arena()
        val obj = arena.allocPointerTo<git_object>()
        git_object_peel(obj.ptr, handler, ObjectType.Tree.value).errorCheck()
        val target = obj.ptr.reinterpret<git_tree>()
        TODO("Not yet implemented")
    }

    fun asBlob(): Unit? {
        if (type != ObjectType.Blob) return null
        val target = handler.reinterpret<git_blob>()
        TODO("Not yet implemented")
    }

    fun asCommit(): Unit? {
        if (type != ObjectType.Commit) return null
        val target = handler.reinterpret<git_commit>()
        TODO("Not yet implemented")
    }

    fun asTag(): Unit? {
        if (type != ObjectType.Tag) return null
        val target = handler.reinterpret<git_tag>()
        TODO("Not yet implemented")
    }

    fun asTree(): Unit? {
        if (type != ObjectType.Tree) return null
        val target = handler.reinterpret<git_tree>()
        TODO("Not yet implemented")
    }
}
