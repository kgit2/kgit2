package com.kgit2.reflog

import cnames.structs.git_reflog
import com.kgit2.annotations.Raw
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.extend.toInt
import com.kgit2.common.memory.Memory
import com.kgit2.memory.IterableBase
import com.kgit2.memory.RawWrapper
import com.kgit2.oid.Oid
import com.kgit2.signature.Signature
import libgit2.*

@Raw(
    base = git_reflog::class,
    free = "git_reflog_free",
)
class Reflog(raw: ReflogRaw) : RawWrapper<git_reflog, ReflogRaw>(raw), IterableBase<ReflogEntry> {
    constructor(secondaryInitial: ReflogSecondaryInitial) : this(ReflogRaw(secondaryInitial = secondaryInitial))

    fun append(oid: Oid, committer: Signature, message: String? = null) {
        git_reflog_append(raw.handler, oid.raw.handler, committer.raw.handler, message).errorCheck()
    }

    fun drop(index: ULong, rewritePreviousEntry: Boolean) {
        git_reflog_drop(raw.handler, index, rewritePreviousEntry.toInt()).errorCheck()
    }

    fun write() {
        git_reflog_write(raw.handler).errorCheck()
    }

    operator fun get(index: ULong): ReflogEntry =
        git_reflog_entry_byindex(raw.handler, index)?.let { ReflogEntry(ReflogEntryRaw(Memory(), it)) }
            ?: throw IndexOutOfBoundsException()

    override val size: Long
        get() = git_reflog_entrycount(raw.handler).toLong()

    override fun get(index: Long): ReflogEntry =
        git_reflog_entry_byindex(raw.handler, index.toULong())?.let { ReflogEntry(ReflogEntryRaw(Memory(), it)) }
            ?: throw IndexOutOfBoundsException()
}
