package com.kgit2.diff

import libgit2.GIT_DELTA_ADDED
import libgit2.GIT_DELTA_CONFLICTED
import libgit2.GIT_DELTA_COPIED
import libgit2.GIT_DELTA_DELETED
import libgit2.GIT_DELTA_IGNORED
import libgit2.GIT_DELTA_MODIFIED
import libgit2.GIT_DELTA_RENAMED
import libgit2.GIT_DELTA_TYPECHANGE
import libgit2.GIT_DELTA_UNMODIFIED
import libgit2.GIT_DELTA_UNREADABLE
import libgit2.GIT_DELTA_UNTRACKED
import libgit2.git_delta_t

enum class DiffDeltaType(val value: git_delta_t) {
    Unmodified(GIT_DELTA_UNMODIFIED),
    Added(GIT_DELTA_ADDED),
    Deleted(GIT_DELTA_DELETED),
    Modified(GIT_DELTA_MODIFIED),
    Renamed(GIT_DELTA_RENAMED),
    Copied(GIT_DELTA_COPIED),
    Ignored(GIT_DELTA_IGNORED),
    Untracked(GIT_DELTA_UNTRACKED),
    TypeChange(GIT_DELTA_TYPECHANGE),
    Unreadable(GIT_DELTA_UNREADABLE),
    Conflicted(GIT_DELTA_CONFLICTED),
    ;

    companion object {
        fun from(value: git_delta_t): DiffDeltaType {
            return when (value) {
                GIT_DELTA_UNMODIFIED -> Unmodified
                GIT_DELTA_ADDED -> Added
                GIT_DELTA_DELETED -> Deleted
                GIT_DELTA_MODIFIED -> Modified
                GIT_DELTA_RENAMED -> Renamed
                GIT_DELTA_COPIED -> Copied
                GIT_DELTA_IGNORED -> Ignored
                GIT_DELTA_UNTRACKED -> Untracked
                GIT_DELTA_TYPECHANGE -> TypeChange
                GIT_DELTA_UNREADABLE -> Unreadable
                GIT_DELTA_CONFLICTED -> Conflicted
                else -> throw IllegalArgumentException("Unknown delta type: $value")
            }
        }
    }
}
