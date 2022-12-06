package com.kgit2.rebase

import libgit2.GIT_REBASE_OPERATION_EDIT
import libgit2.GIT_REBASE_OPERATION_EXEC
import libgit2.GIT_REBASE_OPERATION_FIXUP
import libgit2.GIT_REBASE_OPERATION_PICK
import libgit2.GIT_REBASE_OPERATION_REWORD
import libgit2.GIT_REBASE_OPERATION_SQUASH
import libgit2.git_rebase_operation_t

enum class RebaseOperationType(val value: git_rebase_operation_t) {
    Pick(GIT_REBASE_OPERATION_PICK),
    Reword(GIT_REBASE_OPERATION_REWORD),
    Edit(GIT_REBASE_OPERATION_EDIT),
    Squash(GIT_REBASE_OPERATION_SQUASH),
    Fixup(GIT_REBASE_OPERATION_FIXUP),
    Exec(GIT_REBASE_OPERATION_EXEC),
    ;

    companion object {
        fun from(value: git_rebase_operation_t): RebaseOperationType {
            return when (value) {
                GIT_REBASE_OPERATION_PICK -> Pick
                GIT_REBASE_OPERATION_REWORD -> Reword
                GIT_REBASE_OPERATION_EDIT -> Edit
                GIT_REBASE_OPERATION_SQUASH -> Squash
                GIT_REBASE_OPERATION_FIXUP -> Fixup
                GIT_REBASE_OPERATION_EXEC -> Exec
                else -> throw IllegalArgumentException("Unknown value: $value")
            }
        }
    }
}
