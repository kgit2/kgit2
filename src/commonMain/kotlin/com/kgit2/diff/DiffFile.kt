package com.kgit2.diff

import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.common.option.mutually.FileMode
import com.kgit2.memory.RawWrapper
import com.kgit2.oid.Oid
import kotlinx.cinterop.convert
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import libgit2.git_diff_file

@Raw(
    base = git_diff_file::class,
)
class DiffFile(raw: DiffFileRaw) : RawWrapper<git_diff_file, DiffFileRaw>(raw) {
    constructor(memory: Memory, handler: DiffFilePointer) : this(DiffFileRaw(memory, handler))

    val id: Oid = Oid(Memory(), raw.handler.pointed.id.ptr)

    val path: String? = raw.handler.pointed.path?.toKString()

    val size: ULong = raw.handler.pointed.size

    val flag: DiffFlags = DiffFlags(raw.handler.pointed.flags)

    val mod: FileMode = FileMode.fromRaw(raw.handler.pointed.mode.convert())

    val flags: DiffFlags = DiffFlags(raw.handler.pointed.flags)

    override fun toString(): String {
        return "DiffFile(id=$id, path=$path, size=$size, flag=$flag, mod=$mod, flags=$flags)"
    }
}
