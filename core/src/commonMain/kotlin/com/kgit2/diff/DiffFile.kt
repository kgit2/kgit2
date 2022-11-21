package com.kgit2.diff

import com.kgit2.common.memory.Memory
import com.kgit2.common.option.mutually.FileMode
import com.kgit2.model.Oid
import kotlinx.cinterop.convert
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import libgit2.git_diff_file

// typealias DiffFilePointer = CPointer<git_diff_file>
//
// typealias DiffFileSecondaryPointer = CPointerVar<git_diff_file>
//
// typealias DiffFileInitial = DiffFilePointer.(Memory) -> Unit
//
// class DiffFileRaw(
//     memory: Memory,
//     handler: DiffFilePointer,
// ) : Binding<git_diff_file>(memory, handler) {
//     constructor(
//         memory: Memory = Memory(),
//         handler: DiffFileSecondaryPointer = memory.allocPointerTo(),
//         initial: DiffFileInitial? = null,
//     ) : this(memory, handler.apply {
//         runCatching {
//             initial?.invoke(handler, memory)
//         }.onFailure {
//             memory.free()
//         }.getOrThrow()
//     }.value!!)
// }

data class DiffFile(
    val id: Oid,
    val path: String?,
    val size: ULong,
    val flag: DiffFlag,
    val mod: FileMode,
    val isBinary: Boolean = flag in DiffFlag.Binary,
    val isNotBinary: Boolean = flag in DiffFlag.NotBinary,
    val isValidId: Boolean = flag in DiffFlag.ValidID,
    val exists: Boolean = flag in DiffFlag.Exists,
) {
    constructor(handler: git_diff_file) : this(
        id = Oid(Memory(), handler.id.ptr),
        path = handler.path?.toKString(),
        size = handler.size,
        flag = DiffFlag(handler.flags),
        mod = FileMode.fromRaw(handler.mode.convert()),
    )
}
