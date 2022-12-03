package com.kgit2.reflog

import com.kgit2.annotations.Raw
import com.kgit2.memory.RawWrapper
import com.kgit2.oid.Oid
import com.kgit2.signature.Signature
import kotlinx.cinterop.toKString
import libgit2.*

@Raw(
    base = git_reflog_entry::class,
    free = "git_reflog_entry__free"
)
class ReflogEntry(raw: ReflogEntryRaw) : RawWrapper<git_reflog_entry, ReflogEntryRaw>(raw) {
    constructor(secondaryInitial: ReflogEntrySecondaryInitial) : this(ReflogEntryRaw(secondaryInitial = secondaryInitial))

    val committer: Signature? = git_reflog_entry_committer(raw.handler)?.let { Signature(handler = it) }

    val newId: Oid? = git_reflog_entry_id_new(raw.handler)?.let { Oid(handler = it) }

    val oldId: Oid? = git_reflog_entry_id_old(raw.handler)?.let { Oid(handler = it) }

    val message: String? = git_reflog_entry_message(raw.handler)?.toKString()
}