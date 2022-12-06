package com.kgit2.note

import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.memory.IteratorBase
import com.kgit2.memory.RawWrapper
import com.kgit2.oid.Oid
import kotlinx.cinterop.allocPointerTo
import libgit2.git_note_iterator
import libgit2.git_note_next
import kotlin.native.ref.WeakReference

@Raw(
    base = git_note_iterator::class,
    free = "git_note_iterator_free",
)
class NoteIterator(raw: IteratorRaw) : RawWrapper<git_note_iterator, IteratorRaw>(raw), IteratorBase<Pair<Oid, Oid>> {
    constructor(
        memory: Memory = Memory(),
        secondary: IteratorSecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: IteratorSecondaryInitial? = null,
    ) : this(IteratorRaw(memory, secondary, secondaryInitial))

    override var next: WeakReference<Pair<Oid, Oid>>? = null

    override fun nextRaw(): Result<Pair<Oid, Oid>> = runCatching {
        lateinit var noteId: Oid
        lateinit var annotatedId: Oid
        noteId = Oid note@{
            annotatedId = Oid annotated@{
                git_note_next(this@note, this@annotated, raw.handler)
            }
        }
        noteId to annotatedId
    }
}
