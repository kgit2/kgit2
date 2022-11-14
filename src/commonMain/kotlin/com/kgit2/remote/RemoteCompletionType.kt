package com.kgit2.remote

import libgit2.git_remote_completion_t

enum class RemoteCompletionType {
    GIT_REMOTE_COMPLETION_DOWNLOAD,
    GIT_REMOTE_COMPLETION_INDEXING,
    GIT_REMOTE_COMPLETION_ERROR;

    fun toRaw(): git_remote_completion_t {
        return when (this) {
            GIT_REMOTE_COMPLETION_DOWNLOAD -> git_remote_completion_t.GIT_REMOTE_COMPLETION_DOWNLOAD
            GIT_REMOTE_COMPLETION_INDEXING -> git_remote_completion_t.GIT_REMOTE_COMPLETION_INDEXING
            GIT_REMOTE_COMPLETION_ERROR -> git_remote_completion_t.GIT_REMOTE_COMPLETION_ERROR
        }
    }

    companion object {
        fun fromRaw(raw: git_remote_completion_t): RemoteCompletionType {
            return when (raw) {
                git_remote_completion_t.GIT_REMOTE_COMPLETION_DOWNLOAD -> GIT_REMOTE_COMPLETION_DOWNLOAD
                git_remote_completion_t.GIT_REMOTE_COMPLETION_INDEXING -> GIT_REMOTE_COMPLETION_INDEXING
                git_remote_completion_t.GIT_REMOTE_COMPLETION_ERROR -> GIT_REMOTE_COMPLETION_ERROR
                else -> error("Unknown remote completion type: $raw")
            }
        }
    }
}
