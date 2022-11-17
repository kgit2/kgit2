package com.kgit2.worktree

import com.kgit2.common.error.toBoolean
import com.kgit2.common.error.toInt
import com.kgit2.common.option.BaseMultiple
import com.kgit2.model.AutoFreeGitBase
import com.kgit2.reference.Reference
import kotlinx.cinterop.*
import libgit2.*

class WorktreePruneOptions(
    override val handler: CPointer<git_worktree_prune_options>,
    override val arena: Arena,
) : AutoFreeGitBase<CPointer<git_worktree_prune_options>> {
    companion object {
        fun initialize(): WorktreePruneOptions {
            val arena = Arena()
            val handler = arena.allocPointerTo<git_worktree_prune_options>()
            git_worktree_prune_options_init(handler.value, GIT_WORKTREE_PRUNE_OPTIONS_VERSION)
            return WorktreePruneOptions(handler.value!!, arena)
        }
    }

    private fun flag(flag: WorktreePruneOptionsFlag, on: Boolean): WorktreePruneOptions {
        when (on) {
            true -> handler.pointed.flags = handler.pointed.flags or flag.value
            false -> handler.pointed.flags = handler.pointed.flags and flag.value.inv()
        }
        return this
    }

    fun valid(valid: Boolean): WorktreePruneOptions = flag(WorktreePruneOptionsFlag.Valid, valid)

    fun locked(locked: Boolean): WorktreePruneOptions = flag(WorktreePruneOptionsFlag.Locked, locked)

    fun workingTree(workingTree: Boolean): WorktreePruneOptions =
        flag(WorktreePruneOptionsFlag.WorkingTree, workingTree)
}

data class WorktreePruneOptionsFlag(val value: git_worktree_prune_t) : BaseMultiple<WorktreePruneOptionsFlag>() {
    companion object {
        val Valid = WorktreePruneOptionsFlag(GIT_WORKTREE_PRUNE_VALID)
        val Locked = WorktreePruneOptionsFlag(GIT_WORKTREE_PRUNE_LOCKED)
        val WorkingTree = WorktreePruneOptionsFlag(GIT_WORKTREE_PRUNE_WORKING_TREE)
    }

    override val longValue: ULong
        get() = value.toULong()
}

class WorktreeAddOptions(
    override val handler: CPointer<git_worktree_add_options>,
    override val arena: Arena,
) : AutoFreeGitBase<CPointer<git_worktree_add_options>> {
    companion object {
        fun initialize(): WorktreeAddOptions {
            val arena = Arena()
            val handler = arena.allocPointerTo<git_worktree_add_options>()
            git_worktree_add_options_init(handler.value, GIT_WORKTREE_ADD_OPTIONS_VERSION)
            return WorktreeAddOptions(handler.value!!, arena)
        }
    }

    var lock: Boolean = handler.pointed.lock.toBoolean()
        set(value) {
            field = value
            handler.pointed.lock = lock.toInt()
        }

    var reference: Reference? = handler.pointed.ref?.let { Reference(it, arena) }
        set(value) {
            field = value
            handler.pointed.ref = reference?.handler
        }
}
