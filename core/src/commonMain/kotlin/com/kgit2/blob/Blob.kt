package com.kgit2.blob

import cnames.structs.git_blob
import cnames.structs.git_object
import com.kgit2.common.error.toBoolean
import com.kgit2.common.memory.Memory
import com.kgit2.memory.Binding
import com.kgit2.memory.GitBase
import com.kgit2.model.Oid
import com.kgit2.`object`.Object
import kotlinx.cinterop.*
import libgit2.*
import kotlin.native.internal.Cleaner
import kotlin.native.internal.createCleaner

typealias BlobPointer = CPointer<git_blob>

typealias BlobSecondaryPointer = CPointerVar<git_blob>

typealias BlobInitial = BlobSecondaryPointer.(Memory) -> Unit

class BlobRaw(
    memory: Memory,
    handler: BlobPointer,
) : Binding<git_blob>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: BlobSecondaryPointer = memory.allocPointerTo(),
        initial: BlobInitial? = null,
    ) : this(memory, handler.apply {
        runCatching {
            initial?.invoke(handler, memory)
        }.onFailure {
            git_blob_free(handler.value)
            memory.free()
        }.getOrThrow()
    }.value!!)

    override val beforeFree: () -> Unit = {
        git_blob_free(handler)
    }
}

class Blob(
    raw: BlobRaw,
) : GitBase<git_blob, BlobRaw>(raw) {
    constructor(
        memory: Memory,
        handler: CPointer<git_blob>,
    ) : this(BlobRaw(memory, handler))

    val id: Oid = Oid(raw.memory, git_blob_id(raw.handler)!!)

    val isBinary: Boolean = git_blob_is_binary(raw.handler).toBoolean()

    val size: ULong = git_blob_rawsize(raw.handler)

    val content: ByteArray = git_blob_rawcontent(raw.handler)!!.readBytes(size.convert())

    fun asObject(): Object {
        val `object` = raw.handler.reinterpret<git_object>()
        raw.freed.compareAndSet(expect = false, update = true)
        return Object(raw.memory, `object`)
    }
}
