package com.kgit2.repository

import libgit2.git_repository_state_t

enum class RepositoryState(val value: git_repository_state_t) {
    None(git_repository_state_t.GIT_REPOSITORY_STATE_NONE),
    Merge(git_repository_state_t.GIT_REPOSITORY_STATE_MERGE),
    Revert(git_repository_state_t.GIT_REPOSITORY_STATE_REVERT),
    RevertSequence(git_repository_state_t.GIT_REPOSITORY_STATE_REVERT_SEQUENCE),
    CherryPick(git_repository_state_t.GIT_REPOSITORY_STATE_CHERRYPICK),
    CherryPickSequence(git_repository_state_t.GIT_REPOSITORY_STATE_CHERRYPICK_SEQUENCE),
    Bisect(git_repository_state_t.GIT_REPOSITORY_STATE_BISECT),
    Rebase(git_repository_state_t.GIT_REPOSITORY_STATE_REBASE),
    RebaseInteractive(git_repository_state_t.GIT_REPOSITORY_STATE_REBASE_INTERACTIVE),
    RebaseMerge(git_repository_state_t.GIT_REPOSITORY_STATE_REBASE_MERGE),
    ApplyMailbox(git_repository_state_t.GIT_REPOSITORY_STATE_APPLY_MAILBOX),
    ApplyMailboxOrRebase(git_repository_state_t.GIT_REPOSITORY_STATE_APPLY_MAILBOX_OR_REBASE),
    ;

    companion object {
        fun fromRaw(value: git_repository_state_t): RepositoryState {
            return when (value) {
                git_repository_state_t.GIT_REPOSITORY_STATE_NONE -> None
                git_repository_state_t.GIT_REPOSITORY_STATE_MERGE -> Merge
                git_repository_state_t.GIT_REPOSITORY_STATE_REVERT -> Revert
                git_repository_state_t.GIT_REPOSITORY_STATE_REVERT_SEQUENCE -> RevertSequence
                git_repository_state_t.GIT_REPOSITORY_STATE_CHERRYPICK -> CherryPick
                git_repository_state_t.GIT_REPOSITORY_STATE_CHERRYPICK_SEQUENCE -> CherryPickSequence
                git_repository_state_t.GIT_REPOSITORY_STATE_BISECT -> Bisect
                git_repository_state_t.GIT_REPOSITORY_STATE_REBASE -> Rebase
                git_repository_state_t.GIT_REPOSITORY_STATE_REBASE_INTERACTIVE -> RebaseInteractive
                git_repository_state_t.GIT_REPOSITORY_STATE_REBASE_MERGE -> RebaseMerge
                git_repository_state_t.GIT_REPOSITORY_STATE_APPLY_MAILBOX -> ApplyMailbox
                git_repository_state_t.GIT_REPOSITORY_STATE_APPLY_MAILBOX_OR_REBASE -> ApplyMailboxOrRebase
                else -> throw IllegalArgumentException("Unknown value: $value")
            }
        }
    }
}
