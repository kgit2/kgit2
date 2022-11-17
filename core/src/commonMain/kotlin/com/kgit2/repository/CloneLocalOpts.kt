package com.kgit2.repository

import libgit2.git_clone_local_t
import libgit2.git_clone_local_t.*

enum class CloneLocalOpts(val value: git_clone_local_t) {
    /// Auto-detect (default)
    ///
    /// Here libgit2 will bypass the git-aware transport for local paths, but
    /// use a normal fetch for `file://` urls.
    LocalAuto(GIT_CLONE_LOCAL_AUTO),

    /// Bypass the git-aware transport even for `file://` urls.
    Local(GIT_CLONE_LOCAL),

    /// Never bypass the git-aware transport
    NoLocal(GIT_CLONE_NO_LOCAL),

    /// Bypass the git-aware transport, but don't try to use hardlinks.
    LocalNoLinks(GIT_CLONE_LOCAL_NO_LINKS),
}
