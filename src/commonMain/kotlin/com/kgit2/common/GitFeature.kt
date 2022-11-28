package com.kgit2.common

import com.kgit2.annotations.FlagMask
import libgit2.git_feature_t

@FlagMask(
    flagsType = git_feature_t::class,
    /**
     * If set, libgit2 was built thread-aware and can be safely used from multiple
     * threads.
     */
    "GIT_FEATURE_THREADS",
    /**
     * If set, libgit2 was built with and linked against a TLS implementation.
     * Custom TLS streams may still be added by the user to support HTTPS
     * regardless of this.
     */
    "GIT_FEATURE_HTTPS",
    /**
     * If set, libgit2 was built with and linked against libssh2. A custom
     * transport may still be added by the user to support libssh2 regardless of
     * this.
     */
    "GIT_FEATURE_SSH",
    /**
     * If set, libgit2 was built with support for sub-second resolution in file
     * modification times.
     */
    "GIT_FEATURE_NSEC",
    flagsMutable = false,
)
data class GitFeature(override val flags: git_feature_t) : GitFeatureMask<GitFeature> {
    fun enableThreads(): Boolean = hasFeatureThreads()

    fun enableHttps(): Boolean = hasFeatureHttps()

    fun enableSSH(): Boolean = hasFeatureSsh()

    fun enableNSEC(): Boolean = hasFeatureNsec()
}
