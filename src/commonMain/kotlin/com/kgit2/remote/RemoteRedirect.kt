package com.kgit2.remote

import libgit2.GIT_REMOTE_REDIRECT_ALL
import libgit2.GIT_REMOTE_REDIRECT_INITIAL
import libgit2.GIT_REMOTE_REDIRECT_NONE
import libgit2.git_remote_redirect_t

enum class RemoteRedirect(val value: git_remote_redirect_t) {
    Initial(GIT_REMOTE_REDIRECT_INITIAL),
    None(GIT_REMOTE_REDIRECT_NONE),
    All(GIT_REMOTE_REDIRECT_ALL);

    companion object {
        fun from(value: git_remote_redirect_t): RemoteRedirect {
            return when (value) {
                GIT_REMOTE_REDIRECT_INITIAL -> Initial
                GIT_REMOTE_REDIRECT_NONE -> None
                GIT_REMOTE_REDIRECT_ALL -> All
                else -> error("Unknown value: $value")
            }
        }
    }
}
