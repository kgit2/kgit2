package com.kgit2.rebase

import cnames.structs.git_rebase
import com.kgit2.annotations.Raw
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.index.Index
import com.kgit2.memory.IteratorBase
import com.kgit2.memory.RawWrapper
import com.kgit2.oid.Oid
import com.kgit2.signature.Signature
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import libgit2.*

@Raw(
    base = git_rebase::class,
    free = "git_rebase_free",
)
class Rebase(raw: RebaseRaw) : RawWrapper<git_rebase, RebaseRaw>(raw), Iterable<RebaseOperation> {
    constructor(
        memory: Memory = Memory(),
        secondary: RebaseSecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: RebaseSecondaryInitial? = null,
    ) : this(RebaseRaw(memory, secondary, secondaryInitial))

    val size: ULong = git_rebase_operation_entrycount(raw.handler)

    val originHeadName: String? = git_rebase_orig_head_name(raw.handler)?.toKString()

    val originHeadId: Oid? = git_rebase_orig_head_id(raw.handler)?.let { Oid(handler = it) }

    operator fun get(index: ULong): RebaseOperation {
        val operation = git_rebase_operation_byindex(raw.handler, index)!!
        return RebaseOperation(handler = operation)
    }

    fun currentIndex(): ULong = git_rebase_operation_current(raw.handler)

    fun inMemoryIndex(): Index = Index {
        git_rebase_inmemory_index(this.ptr, raw.handler).errorCheck()
    }

    fun commit(committer: Signature, author: Signature?, message: String?): Oid = Oid {
        git_rebase_commit(this, raw.handler, committer.raw.handler, author?.raw?.handler, null, message).errorCheck()
    }

    fun abort() = git_rebase_abort(raw.handler).errorCheck()

    fun finish(signature: Signature?) {
        git_rebase_finish(raw.handler, signature?.raw?.handler).errorCheck()
    }

    override fun iterator(): Iterator<RebaseOperation> = InnerIterator()

    inner class InnerIterator : IteratorBase<RebaseOperation>() {
        override fun nextRaw(): Result<RebaseOperation> = runCatching {
            RebaseOperation(secondaryInitial = {
                git_rebase_next(this.ptr, raw.handler).errorCheck()
            })
        }
    }
}
