package com.kgit2.diff

import com.kgit2.common.option.mutually.FileMode
import com.kgit2.model.AutoFreeGitBase
import com.kgit2.model.Oid
import kotlinx.cinterop.Arena
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import libgit2.git_diff_file

data class DiffFile(
    val id: Oid,
    val path: String?,
    val size: ULong,
    val flag: DiffFlag,
    val isBinary: Boolean,
    val isNotBinary: Boolean,
    val isValidId: Boolean,
    val exists: Boolean,
    val mod: FileMode,
    override val handler: CPointer<git_diff_file>,
    override val arena: Arena,
) : AutoFreeGitBase<CPointer<git_diff_file>> {
    companion object {
        fun fromHandler(handler: git_diff_file, arena: Arena): DiffFile {
            val flag = DiffFlag(handler.flags)
            return DiffFile(
                Oid(handler.id.ptr, arena),
                handler.path?.toKString(),
                handler.size,
                flag,
                flag in DiffFlag.Binary,
                flag in DiffFlag.NotBinary,
                flag in DiffFlag.ValidID,
                flag in DiffFlag.Exists,
                FileMode.fromRaw(handler.mode.toUInt()),
                handler.ptr,
                arena,
            )
        }
    }
}
