package com.kgit2.worktree

import com.kgit2.annotations.Raw
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.extend.toBoolean
import com.kgit2.common.extend.toInt
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import com.kgit2.reference.Reference
import kotlinx.cinterop.pointed
import libgit2.GIT_WORKTREE_ADD_OPTIONS_VERSION
import libgit2.git_worktree_add_options
import libgit2.git_worktree_add_options_init

@Raw(
    base = git_worktree_add_options::class,
)
class WorktreeAddOptions(
    raw: WorktreeAddOptionsRaw = WorktreeAddOptionsRaw(initial = {
        git_worktree_add_options_init(this, GIT_WORKTREE_ADD_OPTIONS_VERSION).errorCheck()
    }),
) : GitBase<git_worktree_add_options, WorktreeAddOptionsRaw>(raw) {
    constructor(memory: Memory, handler: WorktreeAddOptionsPointer) : this(WorktreeAddOptionsRaw(memory, handler))

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
