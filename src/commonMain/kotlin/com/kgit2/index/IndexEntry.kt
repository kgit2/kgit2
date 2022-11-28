package com.kgit2.index

import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import com.kgit2.oid.Oid
import kotlinx.cinterop.*
import libgit2.git_index_entry
import libgit2.git_oid_cpy

@Raw(
    base = git_index_entry::class,
)
class IndexEntry(raw: IndexEntryRaw) : GitBase<git_index_entry, IndexEntryRaw>(raw) {
    constructor(memory: Memory, raw: IndexEntryValue) : this(IndexEntryRaw(memory, raw))

    constructor(
        memory: Memory = Memory(),
        handler: IndexEntryPointer = memory.alloc<git_index_entry>().ptr,
        initial: IndexEntryInitial? = null
    ) : this(IndexEntryRaw(memory, handler, initial))

    constructor(
        memory: Memory = Memory(),
        secondary: IndexEntrySecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: IndexEntrySecondaryInitial? = null
    ) : this(IndexEntryRaw(memory, secondary, secondaryInitial))

    constructor(
        ctime: IndexTime,
        mtime: IndexTime,
        dev: UInt,
        ino: UInt,
        mode: UInt,
        uid: UInt,
        gid: UInt,
        fileSize: UInt,
        id: Oid,
        flags: UShort,
        flagsExtended: UShort,
        path: String?,
        memory: Memory = Memory(),
    ) : this(memory, cValue<git_index_entry> {
        this.ctime.seconds = ctime.seconds
        this.ctime.nanoseconds = ctime.nanoseconds
        this.mtime.seconds = mtime.seconds
        this.mtime.nanoseconds = mtime.nanoseconds
        this.dev = dev
        this.ino = ino
        this.mode = mode
        this.uid = uid
        this.gid = gid
        this.file_size = fileSize
        git_oid_cpy(this.id.ptr, id.raw.handler)
        this.flags = flags
        this.flags_extended = flagsExtended
        this.path = path?.cstr?.getPointer(memory)
    })

    val ctime: IndexTime = IndexTime(raw.memory, raw.handler.pointed.ctime)

    val mtime: IndexTime = IndexTime(raw.memory, raw.handler.pointed.mtime)

    val dev: UInt = raw.handler.pointed.dev

    val ino: UInt = raw.handler.pointed.ino

    val mode: UInt = raw.handler.pointed.mode

    val uid: UInt = raw.handler.pointed.uid

    val gid: UInt = raw.handler.pointed.gid

    val fileSize: UInt = raw.handler.pointed.file_size

    val id: Oid = Oid(raw.memory, raw.handler.pointed.id)

    val flags: UShort = raw.handler.pointed.flags

    val flagsExtended: UShort = raw.handler.pointed.flags_extended

    val path: String? = raw.handler.pointed.path?.toKString()

    fun memCopy(): IndexEntry = IndexEntry(
        ctime,
        mtime,
        dev,
        ino,
        mode,
        uid,
        gid,
        fileSize,
        id,
        flags,
        flagsExtended,
        path,
    )
}
