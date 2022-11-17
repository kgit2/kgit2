package com.kgit2.blob

import com.kgit2.common.error.toBoolean
import com.kgit2.model.AutoFreeGitBase
import com.kgit2.model.Oid
import com.kgit2.`object`.Object
import kotlinx.cinterop.*
import libgit2.*

class Blob(
    override val handler: CPointer<git_blob>,
    override val arena: Arena,
) : AutoFreeGitBase<CPointer<git_blob>> {
    override fun free() {
        git_blob_free(handler)
        super.free()
    }

    val id: Oid = Oid(git_blob_id(handler)!!, arena)

    val isBinary: Boolean = git_blob_is_binary(handler).toBoolean()

    val size: ULong = git_blob_rawsize(handler)

    val content: ByteArray = git_blob_rawcontent(handler)!!.readBytes(size.convert())

    fun asObject(): Object {
        val raw = handler.reinterpret<git_object>()
        return Object(raw, arena)
    }
}
