package com.kgit2.odb

import cnames.structs.git_odb_object
import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import com.kgit2.model.Oid
import com.kgit2.`object`.ObjectType
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.readBytes
import libgit2.git_odb_object_data
import libgit2.git_odb_object_id
import libgit2.git_odb_object_size
import libgit2.git_odb_object_type

@Raw(
    base = "git_odb_object",
    free = "git_odb_object_free",
)
class OdbObject(
    raw: OdbObjectRaw,
) : GitBase<git_odb_object, OdbObjectRaw>(raw) {
    constructor(memory: Memory, handler: OdbObjectPointer) : this(OdbObjectRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: OdbObjectSecondaryPointer = memory.allocPointerTo(),
        initial: OdbObjectInitial? = null,
    ) : this(OdbObjectRaw(memory, handler, initial))

    val oid: Oid = Oid(Memory(), git_odb_object_id(raw.handler)!!)

    val size: Int = git_odb_object_size(raw.handler).toInt()

    val data: ByteArray = git_odb_object_data(raw.handler)!!.readBytes(size)

    val type: ObjectType = ObjectType.fromRaw(git_odb_object_type(raw.handler))
}
