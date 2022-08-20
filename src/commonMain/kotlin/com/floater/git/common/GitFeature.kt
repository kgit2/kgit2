package com.floater.git.common

enum class GitFeature(val value: Int) {
    /**
     * If set, libgit2 was built thread-aware and can be safely used from multiple
     * threads.
     */
    GIT_FEATURE_THREADS(1 shl 0),
    /**
     * If set, libgit2 was built with and linked against a TLS implementation.
     * Custom TLS streams may still be added by the user to support HTTPS
     * regardless of this.
     */
    GIT_FEATURE_HTTPS(1 shl 1),
    /**
     * If set, libgit2 was built with and linked against libssh2. A custom
     * transport may still be added by the user to support libssh2 regardless of
     * this.
     */
    GIT_FEATURE_SSH(1 shl 2),
    /**
     * If set, libgit2 was built with support for sub-second resolution in file
     * modification times.
     */
    GIT_FEATURE_NSEC(1 shl 3)
}
