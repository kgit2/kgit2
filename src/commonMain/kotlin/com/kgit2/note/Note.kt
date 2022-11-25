package com.kgit2.note

import cnames.structs.git_note
import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import com.kgit2.model.Oid
import com.kgit2.signature.Signature
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import libgit2.git_note_author
import libgit2.git_note_committer
import libgit2.git_note_id
import libgit2.git_note_message

@Raw(
    base = "git_note",
    free = "git_note_free",
)
class Note(raw: NoteRaw) : GitBase<git_note, NoteRaw>(raw) {
    constructor(memory: Memory, handler: NotePointer) : this(NoteRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: NoteSecondaryPointer = memory.allocPointerTo(),
        initial: NoteInitial? = null,
    ) : this(NoteRaw(memory, handler, initial))

    val author = Signature { this.value = git_note_author(raw.handler) }

    val committer = Signature { this.value = git_note_committer(raw.handler) }

    val message = git_note_message(raw.handler)?.toKString()

    val id = Oid(Memory(), git_note_id(raw.handler)!!)
}
