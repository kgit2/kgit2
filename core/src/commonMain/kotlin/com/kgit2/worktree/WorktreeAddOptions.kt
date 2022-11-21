package com.kgit2.worktree

import com.kgit2.common.error.errorCheck
import com.kgit2.common.error.toBoolean
import com.kgit2.common.error.toInt
import com.kgit2.common.memory.Memory
import com.kgit2.memory.Binding
import com.kgit2.memory.GitBase
import com.kgit2.reference.Reference
import kotlinx.cinterop.*
import libgit2.GIT_WORKTREE_ADD_OPTIONS_VERSION
import libgit2.git_worktree_add_options
import libgit2.git_worktree_add_options_init

typealias WorktreeAddOptionsPointer = CPointer<git_worktree_add_options>

typealias WorktreeAddOptionsSecondaryPointer = CPointerVar<git_worktree_add_options>

typealias WorktreeAddOptionsInitial = WorktreeAddOptionsSecondaryPointer.(Memory) -> Unit

class WorktreeAddOptionsRaw(
    memory: Memory,
    handler: WorktreeAddOptionsPointer,
) : Binding<git_worktree_add_options>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: WorktreeAddOptionsSecondaryPointer = memory.allocPointerTo(),
        initial: WorktreeAddOptionsInitial = {
            git_worktree_add_options_init(handler.value, GIT_WORKTREE_ADD_OPTIONS_VERSION).errorCheck()
        },
    ) : this(memory, handler.apply {
        runCatching {
            initial.invoke(handler, memory)
        }.onFailure {
            memory.free()
        }.getOrThrow()
    }.value!!)
}

class WorktreeAddOptions(raw: WorktreeAddOptionsRaw) : GitBase<git_worktree_add_options, WorktreeAddOptionsRaw>(raw) {
    constructor(memory: Memory, handler: WorktreeAddOptionsPointer) : this(WorktreeAddOptionsRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: WorktreeAddOptionsSecondaryPointer = memory.allocPointerTo(),
        initial: WorktreeAddOptionsInitial = {
            git_worktree_add_options_init(handler.value, GIT_WORKTREE_ADD_OPTIONS_VERSION).errorCheck()
        },
    ) : this(WorktreeAddOptionsRaw(memory, handler, initial))


    var lock: Boolean = raw.handler.pointed.lock.toBoolean()
        set(value) {
            field = value
            raw.handler.pointed.lock = field.toInt()
        }

    var reference: Reference? = raw.handler.pointed.ref?.let { Reference(Memory(), it) }
        set(value) {
            field = value
            raw.handler.pointed.ref = field?.raw?.handler
        }
}
