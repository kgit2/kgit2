package com.kgit2.odb

import cnames.structs.git_odb
import com.kgit2.annotations.Raw
import com.kgit2.common.extend.asStableRef
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.extend.toBoolean
import com.kgit2.common.memory.Memory
import com.kgit2.common.memory.memoryScoped
import com.kgit2.memory.RawWrapper
import com.kgit2.oid.Oid
import com.kgit2.`object`.ObjectType
import kotlinx.cinterop.*
import libgit2.*

@Raw(
    base = git_odb::class,
    free = "git_odb_free"
)
class Odb(raw: OdbRaw) : RawWrapper<git_odb, OdbRaw>(raw) {
    constructor(memory: Memory, handler: OdbPointer) : this(OdbRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        secondary: OdbSecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: OdbSecondaryInitial? = null,
    ) : this(OdbRaw(memory, secondary, secondaryInitial))

    constructor() : this(secondaryInitial = {
        git_odb_new(this.ptr).errorCheck()
    })

    fun reader(oid: Oid): Triple<OdbReader, ObjectType, ULong> {
        lateinit var type: ObjectType
        var size: ULong = 0U
        return Triple(OdbReader { memory ->
            val typeVar = memory.alloc<git_object_tVar>()
            typeVar.value = ObjectType.Any.value
            val sizeVar = memory.alloc<ULongVar>()
            git_odb_open_rstream(this.ptr, sizeVar.ptr, typeVar.ptr, raw.handler, oid.raw.handler).errorCheck()
            type = ObjectType.fromRaw(typeVar.value)
            size = sizeVar.value
        }, type, size)
    }

    fun read(oid: Oid): OdbObject = OdbObject {
        git_odb_read(this.ptr, raw.handler, oid.raw.handler).errorCheck()
    }

    fun readHeader(oid: Oid): Pair<ObjectType, ULong> = memoryScoped {
        val typeVar = alloc<git_object_tVar>()
        typeVar.value = ObjectType.Any.value
        val sizeVar = alloc<ULongVar>()
        git_odb_read_header(sizeVar.ptr, typeVar.ptr, raw.handler, oid.raw.handler).errorCheck()
        return Pair(ObjectType.fromRaw(typeVar.value), sizeVar.value)
    }

    fun writer(size: ULong, type: ObjectType): OdbWriter {
        return OdbWriter {
            git_odb_open_wstream(this.ptr, raw.handler, size, type.value).errorCheck()
        }
    }

    fun write(data: ByteArray, type: ObjectType): Oid = Oid {
        git_odb_write(this, raw.handler, data.refTo(0), data.size.toULong(), type.value).errorCheck()
    }

    fun packWriter(): OdbPackWriter = OdbPackWriter.odbWritePack(this)

    fun exists(oid: Oid): Boolean = git_odb_exists(raw.handler, oid.raw.handler).toBoolean()

    fun existsExt(oid: Oid, flags: OdbLookupFlags) =
        git_odb_exists_ext(raw.handler, oid.raw.handler, flags.value).toBoolean()

    fun existsPrefix(shortOid: Oid, len: ULong): Oid = Oid {
        git_odb_exists_prefix(this, raw.handler, shortOid.raw.handler, len).errorCheck()
    }

    fun refresh() = git_odb_refresh(raw.handler).errorCheck()

    fun addDiskAlternate(path: String) = git_odb_add_disk_alternate(raw.handler, path).errorCheck()

    fun addMemPackBackend(priority: Int) {
        val memPack = MemPack { git_mempack_new(this.ptr).errorCheck() }
        git_odb_add_backend(raw.handler, memPack.raw.handler, priority).errorCheck()
    }

    fun forEach(callback: OdbForEachCallback) {
        val callbackPayload = object : OdbForEachCallbackPayload {
            override var odbForEachCallback: OdbForEachCallback? = callback
        }.asStableRef()
        git_odb_foreach(raw.handler, staticOdbForEachCallback, callbackPayload.asCPointer()).errorCheck()
    }
}
