package com.kgit2.note

import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import com.kgit2.memory.Raw
import com.kgit2.model.Oid
import com.kgit2.signature.Signature
import kotlinx.cinterop.*
import libgit2.*

typealias NotePointer = CPointer<git_note>

typealias NoteSecondaryPointer = CPointerVar<git_note>

typealias NoteInitial = NoteSecondaryPointer.(Memory) -> Unit

class NoteRaw(
    memory: Memory,
    handler: NotePointer,
) : Raw<git_note>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: NoteSecondaryPointer = memory.allocPointerTo(),
        initial: NoteInitial? = null,
    ) : this(memory, handler.apply {
        runCatching {
            initial?.invoke(handler, memory)
        }.onFailure {
            git_note_free(handler.value!!)
            memory.free()
        }.getOrThrow()
    }.value!!)

    override val beforeFree: () -> Unit = {
        git_note_free(handler)
    }
}

class Note(raw: NoteRaw) : GitBase<git_note, NoteRaw>(raw) {
    constructor(memory: Memory, handler: NotePointer) : this(NoteRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: NoteSecondaryPointer = memory.allocPointerTo(),
        initial: NoteInitial? = null,
    ) : this(NoteRaw(memory, handler, initial))

    val author = Signature() { this.value = git_note_author(raw.handler) }

    val committer = Signature() { this.value = git_note_committer(raw.handler) }

    val message = git_note_message(raw.handler)?.toKString()

    val id = Oid(Memory(), git_note_id(raw.handler)!!)
}
