package com.kgit2.remote

import libgit2.git_remote_completion_t

enum class RemoteCompletionType(val value: git_remote_completion_t) {
    Download(git_remote_completion_t.GIT_REMOTE_COMPLETION_DOWNLOAD),
    Indexing(git_remote_completion_t.GIT_REMOTE_COMPLETION_INDEXING),
    Error(git_remote_completion_t.GIT_REMOTE_COMPLETION_ERROR),
    ;

    companion object {
        fun from(raw: git_remote_completion_t): RemoteCompletionType {
            return when (raw) {
                git_remote_completion_t.GIT_REMOTE_COMPLETION_DOWNLOAD -> Download
                git_remote_completion_t.GIT_REMOTE_COMPLETION_INDEXING -> Indexing
                git_remote_completion_t.GIT_REMOTE_COMPLETION_ERROR -> Error
                else -> error("Unknown remote completion type: $raw")
            }
        }
    }
}
