package com.floater.git.common.option

import libgit2.git_clone_local_t

enum class CloneLocalOpts(val value: git_clone_local_t) {
    /// Auto-detect (default)
    ///
    /// Here libgit2 will bypass the git-aware transport for local paths, but
    /// use a normal fetch for `file://` urls.
    GIT_CLONE_LOCAL_AUTO(git_clone_local_t.GIT_CLONE_LOCAL_AUTO),

    /// Bypass the git-aware transport even for `file://` urls.
    GIT_CLONE_LOCAL(git_clone_local_t.GIT_CLONE_LOCAL),

    /// Never bypass the git-aware transport
    GIT_CLONE_NO_LOCAL(git_clone_local_t.GIT_CLONE_NO_LOCAL),

    /// Bypass the git-aware transport, but don't try to use hardlinks.
    GIT_CLONE_LOCAL_NO_LINKS(git_clone_local_t.GIT_CLONE_LOCAL_NO_LINKS),
}
