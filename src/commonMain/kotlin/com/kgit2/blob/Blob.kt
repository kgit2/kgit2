package com.kgit2.blob

import cnames.structs.git_blob
import com.kgit2.annotations.Raw
import com.kgit2.common.extend.toBoolean
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import com.kgit2.oid.Oid
import com.kgit2.`object`.Object
import kotlinx.cinterop.convert
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.reinterpret
import libgit2.git_blob_id
import libgit2.git_blob_is_binary
import libgit2.git_blob_rawcontent
import libgit2.git_blob_rawsize

/**
 * In-memory representation of a blob object.
 */
@Raw(
    base = git_blob::class,
    free = "git_blob_free",
)
class Blob(
    raw: BlobRaw,
) : GitBase<git_blob, BlobRaw>(raw) {
    constructor(memory: Memory, handler: BlobPointer) : this(BlobRaw(memory, handler))

    /** */
    val id: Oid = Oid(raw.memory, git_blob_id(raw.handler)!!)

    val isBinary: Boolean = git_blob_is_binary(raw.handler).toBoolean()

    val size: ULong = git_blob_rawsize(raw.handler)

    val content: ByteArray = git_blob_rawcontent(raw.handler)!!.readBytes(size.convert())

    fun asObject(): Object {
        raw.move()
        return Object(raw.memory, raw.handler.reinterpret())
    }
}
