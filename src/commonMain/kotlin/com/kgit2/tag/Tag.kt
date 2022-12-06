package com.kgit2.tag

import cnames.structs.git_tag
import com.kgit2.annotations.Raw
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.RawWrapper
import com.kgit2.`object`.Object
import com.kgit2.`object`.ObjectType
import com.kgit2.oid.Oid
import com.kgit2.signature.Signature
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toKString
import libgit2.git_tag_id
import libgit2.git_tag_message
import libgit2.git_tag_name
import libgit2.git_tag_peel
import libgit2.git_tag_tagger
import libgit2.git_tag_target
import libgit2.git_tag_target_id
import libgit2.git_tag_target_type

@Raw(
    base = git_tag::class,
    free = "git_tag_free",
)
class Tag(raw: TagRaw) : RawWrapper<git_tag, TagRaw>(raw) {
    constructor(memory: Memory, handler: TagPointer) : this(TagRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        secondary: TagSecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: TagSecondaryInitial? = null,
    ) : this(TagRaw(memory, secondary, secondaryInitial))

    val id: Oid = Oid(Memory(), git_tag_id(raw.handler)!!)

    val name: String = git_tag_name(raw.handler)!!.toKString()

    val message: String = git_tag_message(raw.handler)!!.toKString()

    val tagger: Signature = Signature(raw.memory, git_tag_tagger(raw.handler)!!)

    val target: Object = Object { git_tag_target(this.ptr, raw.handler).errorCheck() }

    val targetId: Oid = Oid(Memory(), git_tag_target_id(raw.handler)!!)

    val targetType: ObjectType = ObjectType.fromRaw(git_tag_target_type(raw.handler))

    fun peel(): Object = Object { git_tag_peel(this.ptr, raw.handler).errorCheck() }

    fun asObject(): Object {
        raw.move()
        return Object(raw.memory, raw.handler.reinterpret())
    }
}
