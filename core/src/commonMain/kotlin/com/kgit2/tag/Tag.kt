package com.kgit2.tag

import cnames.structs.git_object
import cnames.structs.git_tag
import com.kgit2.common.error.errorCheck
import com.kgit2.model.AutoFreeGitBase
import com.kgit2.model.Oid
import com.kgit2.`object`.Object
import com.kgit2.`object`.ObjectType
import com.kgit2.signature.Signature
import kotlinx.cinterop.*
import libgit2.*

class Tag(
    override val handler: CPointer<git_tag>,
    override val arena: Arena,
) : AutoFreeGitBase<CPointer<git_tag>> {
    val id: Oid = Oid(git_tag_id(handler)!!, arena)

    val name: String = git_tag_name(handler)!!.toKString()

    val message: String = git_tag_message(handler)!!.toKString()

    val tagger: Signature = Signature.fromHandler(git_tag_tagger(handler)!!, arena)

    val target: Object = run {
        val arena = Arena()
        val obj = arena.allocPointerTo<git_object>()
        git_tag_target(obj.ptr, handler).errorCheck()
        Object(obj.value!!, arena)
    }

    val targetId: Oid = Oid(git_tag_target_id(handler)!!, arena)

    val targetType: ObjectType = ObjectType.fromRaw(git_tag_target_type(handler))

    fun peel(): Object {
        val arena = Arena()
        val obj = arena.allocPointerTo<git_object>()
        git_tag_peel(obj.ptr, handler).errorCheck()
        return Object(obj.value!!, arena)
    }

    fun asObject(): Object {
        return Object(handler.reinterpret(), arena)
    }
}
