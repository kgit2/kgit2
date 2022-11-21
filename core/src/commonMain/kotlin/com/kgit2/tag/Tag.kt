package com.kgit2.tag

import cnames.structs.git_tag
import com.kgit2.common.error.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.Binding
import com.kgit2.memory.GitBase
import com.kgit2.model.Oid
import com.kgit2.`object`.Object
import com.kgit2.`object`.ObjectType
import com.kgit2.signature.Signature
import kotlinx.cinterop.*
import libgit2.*

typealias TagPointer = CPointer<git_tag>

typealias TagSecondaryPointer = CPointerVar<git_tag>

typealias TagInitial = TagSecondaryPointer.(Memory) -> Unit

class TagRaw(
    memory: Memory,
    handler: TagPointer,
) : Binding<git_tag>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: TagSecondaryPointer = memory.allocPointerTo(),
        initial: TagInitial? = null,
    ) : this(memory, handler.apply {
        runCatching {
            initial?.invoke(handler, memory)
        }.onFailure {
            git_tag_free(handler.value!!)
            memory.free()
        }.getOrThrow()
    }.value!!)
}

class Tag(raw: TagRaw) : GitBase<git_tag, TagRaw>(raw) {
    constructor(memory: Memory, handler: TagPointer) : this(TagRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: TagSecondaryPointer = memory.allocPointerTo(),
        initial: TagInitial? = null
    ) : this(TagRaw(memory, handler, initial))

    val id: Oid = Oid(Memory(), git_tag_id(raw.handler)!!)

    val name: String = git_tag_name(raw.handler)!!.toKString()

    val message: String = git_tag_message(raw.handler)!!.toKString()

    val tagger: Signature = Signature(Memory(), git_tag_tagger(raw.handler)!!)

    val target: Object = Object() { git_tag_target(this.ptr, raw.handler).errorCheck() }

    val targetId: Oid = Oid(Memory(), git_tag_target_id(raw.handler)!!)

    val targetType: ObjectType = ObjectType.fromRaw(git_tag_target_type(raw.handler))

    fun peel(): Object = Object() { git_tag_peel(this.ptr, raw.handler).errorCheck() }

    fun asObject(): Object {
        raw.freed.compareAndSet(expect = false, update = true)
        return Object(raw.memory, raw.handler.reinterpret())
    }
}
