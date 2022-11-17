package com.kgit2.odb

import cnames.structs.git_odb_object
import com.kgit2.`object`.ObjectType
import com.kgit2.model.AutoFreeGitBase
import com.kgit2.model.Oid
import kotlinx.cinterop.Arena
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.readBytes
import libgit2.*

class OdbObject(
    override val handler: CPointer<git_odb_object>,
    override val arena: Arena,
) : AutoFreeGitBase<CPointer<git_odb_object>> {
    override fun free() {
        git_odb_object_free(handler)
        super.free()
    }

    val oid: Oid = Oid(git_odb_object_id(handler)!!, arena)

    val size: Int = git_odb_object_size(handler).toInt()

    val data: ByteArray = git_odb_object_data(handler)!!.readBytes(size)

    val type: ObjectType = ObjectType.fromRaw(git_odb_object_type(handler))
}
