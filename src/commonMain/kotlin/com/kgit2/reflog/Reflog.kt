package com.kgit2.reflog

import cnames.structs.git_reflog
import com.kgit2.annotations.Raw
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.extend.toInt
import com.kgit2.common.memory.Memory
import com.kgit2.memory.IterableBase
import com.kgit2.memory.RawWrapper
import com.kgit2.oid.Oid
import com.kgit2.repository.Repository
import com.kgit2.signature.Signature
import kotlinx.cinterop.ptr
import libgit2.git_reflog_append
import libgit2.git_reflog_drop
import libgit2.git_reflog_entry_byindex
import libgit2.git_reflog_entrycount
import libgit2.git_reflog_read
import libgit2.git_reflog_write

@Raw(
    base = git_reflog::class,
    free = "git_reflog_free",
)
class Reflog(raw: ReflogRaw) : RawWrapper<git_reflog, ReflogRaw>(raw), IterableBase<ReflogEntry> {
    constructor(secondaryInitial: ReflogSecondaryInitial) : this(ReflogRaw(secondaryInitial = secondaryInitial))

    constructor(repository: Repository, name: String) : this(secondaryInitial = {
        git_reflog_read(this.ptr, repository.raw.handler, name).errorCheck()
    })

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
