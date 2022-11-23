package com.kgit2.branch

import cnames.structs.git_reference
import com.kgit2.common.error.errorCheck
import com.kgit2.common.error.toBoolean
import com.kgit2.common.error.toInt
import com.kgit2.common.memory.Memory
import com.kgit2.common.memory.memoryScoped
import com.kgit2.memory.GitBase
import com.kgit2.reference.ReferenceInitial
import com.kgit2.reference.ReferencePointer
import com.kgit2.reference.ReferenceRaw
import com.kgit2.reference.ReferenceSecondaryPointer
import kotlinx.cinterop.*
import libgit2.*

// typealias BranchPointer = CPointer<git_reference>
//
// typealias BranchSecondaryPointer = CPointerVar<git_reference>
//
// typealias BranchInitial = BranchSecondaryPointer.(Memory) -> Unit
//
// class BranchRaw(
//     memory: Memory,
//     handler: CPointer<git_reference>,
// ) : Raw<git_reference>(memory, handler) {
//     constructor(
//         memory: Memory = Memory(),
//         handler: BranchSecondaryPointer = memory.allocPointerTo(),
//         initial: BranchInitial? = null,
//     ) : this(memory, handler.apply {
//         runCatching {
//             initial?.invoke(handler, memory)
//         }.onFailure {
//             memory.free()
//         }.getOrThrow()
//     }.value!!)
//
//     override val beforeFree: () -> Unit = {
//         git_reference_free(handler)
//     }
// }

class Branch(raw: ReferenceRaw) : GitBase<git_reference, ReferenceRaw>(raw) {
    constructor(memory: Memory, handler: ReferencePointer) : this(ReferenceRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: ReferenceSecondaryPointer = memory.allocPointerTo(),
        initial: ReferenceInitial? = null,
    ) : this(ReferenceRaw(memory, handler, initial))

    val name: String = memoryScoped {
        val name = allocPointerTo<ByteVar>()
        git_branch_name(name.ptr, raw.handler).errorCheck()
        name.value!!.toKString()
    }

    val upstream: Branch? = runCatching {
        Branch() { git_branch_upstream(this.ptr, raw.handler).errorCheck() }
    }.getOrNull()

    fun wrap(referenceRaw: ReferenceRaw): Branch = Branch(referenceRaw)

    fun unwrap(): ReferenceRaw = raw

    fun delete() = git_branch_delete(raw.handler).errorCheck()

    fun isHead() = git_branch_is_head(raw.handler).toBoolean()

    fun rename(name: String, force: Boolean): Branch = Branch() {
        git_branch_move(this.ptr, raw.handler, name, force.toInt()).errorCheck()
    }

    fun setUpstream(upstreamName: String) = git_branch_set_upstream(raw.handler, upstreamName).errorCheck()

    companion object {
        fun validName(name: String): Boolean = memoryScoped {
            val valid = alloc<IntVar>()
            git_branch_name_is_valid(valid.ptr, name).errorCheck()
            valid.value.toBoolean()
        }
    }
}
