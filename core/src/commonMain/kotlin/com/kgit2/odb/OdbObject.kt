package com.kgit2.odb

import cnames.structs.git_odb_object
import com.kgit2.common.memory.Memory
import com.kgit2.memory.Raw
import com.kgit2.memory.GitBase
import com.kgit2.model.Oid
import com.kgit2.`object`.ObjectType
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.readBytes
import libgit2.*

typealias OdbObjectPointer = CPointer<git_odb_object>

typealias OdbObjectSecondaryPointer = CPointerVar<git_odb_object>

typealias OdbObjectInitial = OdbObjectSecondaryPointer.(Memory) -> Unit

class OdbObjectRaw(
    memory: Memory,
    handler: OdbObjectPointer,
) : Raw<git_odb_object>(memory, handler) {
    override val beforeFree: () -> Unit = {
        git_odb_object_free(handler)
    }
}


class OdbObject(
    raw: OdbObjectRaw,
) : GitBase<git_odb_object, OdbObjectRaw>(raw) {
    val oid: Oid = Oid(Memory(), git_odb_object_id(raw.handler)!!)

    val size: Int = git_odb_object_size(raw.handler).toInt()

    val data: ByteArray = git_odb_object_data(raw.handler)!!.readBytes(size)

    val type: ObjectType = ObjectType.fromRaw(git_odb_object_type(raw.handler))
}
