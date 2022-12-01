package com.kgit2.note

import cnames.structs.git_note
import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.memory.RawWrapper
import com.kgit2.oid.Oid
import com.kgit2.signature.Signature
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import libgit2.git_note_author
import libgit2.git_note_committer
import libgit2.git_note_id
import libgit2.git_note_message

@Raw(
    base = git_note::class,
    free = "git_note_free",
)
class Note(raw: NoteRaw) : RawWrapper<git_note, NoteRaw>(raw) {
    constructor(memory: Memory, handler: NotePointer) : this(NoteRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        secondary: NoteSecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: NoteSecondaryInitial? = null,
    ) : this(NoteRaw(memory, secondary, secondaryInitial))

    val author = Signature { this.value = git_note_author(raw.handler) }

    val committer = Signature { this.value = git_note_committer(raw.handler) }

    val message = git_note_message(raw.handler)?.toKString()

    val id = Oid(Memory(), git_note_id(raw.handler)!!)
}
