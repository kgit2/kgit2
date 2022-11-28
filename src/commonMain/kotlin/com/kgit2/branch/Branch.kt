package com.kgit2.branch

import cnames.structs.git_reference
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.extend.toBoolean
import com.kgit2.common.extend.toInt
import com.kgit2.common.memory.Memory
import com.kgit2.common.memory.memoryScoped
import com.kgit2.memory.GitBase
import com.kgit2.reference.ReferencePointer
import com.kgit2.reference.ReferenceRaw
import com.kgit2.reference.ReferenceSecondaryInitial
import com.kgit2.reference.ReferenceSecondaryPointer
import kotlinx.cinterop.*
import libgit2.*

/**
 * In-memory representation of a reference.
 */
class Branch(raw: ReferenceRaw) : GitBase<git_reference, ReferenceRaw>(raw) {
    constructor(memory: Memory, handler: ReferencePointer) : this(ReferenceRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        secondary: ReferenceSecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: ReferenceSecondaryInitial? = null,
    ) : this(ReferenceRaw(memory, secondary, secondaryInitial))

    /**
     * Given a reference object,
     * this will check that it really is a branch (ie. it lives under "refs/heads/" or "refs/remotes/"),
     * and return the branch part of it.
     * @throws [com.kgit2.common.error.GitError] GIT_EINVALID if the reference isn't either a local or remote branch, otherwise an error code.
     */
    val name: String = memoryScoped {
        val name = allocPointerTo<ByteVar>()
        git_branch_name(name.ptr, raw.handler).errorCheck()
        name.value!!.toKString()
    }

    val upstream: Branch? = runCatching {
        Branch { git_branch_upstream(this.ptr, raw.handler).errorCheck() }
    }.getOrNull()

    fun wrap(referenceRaw: ReferenceRaw): Branch = Branch(referenceRaw)

    fun unwrap(): ReferenceRaw = raw

    fun delete() = git_branch_delete(raw.handler).errorCheck()

    fun isHead() = git_branch_is_head(raw.handler).toBoolean()

    fun rename(name: String, force: Boolean): Branch = Branch {
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
