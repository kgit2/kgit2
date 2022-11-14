package com.kgit2.common

import com.kgit2.common.option.BaseMultiple
import libgit2.*

data class GitFeature(val value: git_feature_t) : BaseMultiple<GitFeature>() {
    companion object {
        /**
         * If set, libgit2 was built thread-aware and can be safely used from multiple
         * threads.
         */
        val Threads = GitFeature(GIT_FEATURE_THREADS)

        /**
         * If set, libgit2 was built with and linked against a TLS implementation.
         * Custom TLS streams may still be added by the user to support HTTPS
         * regardless of this.
         */
        val Https = GitFeature(GIT_FEATURE_HTTPS)

        /**
         * If set, libgit2 was built with and linked against libssh2. A custom
         * transport may still be added by the user to support libssh2 regardless of
         * this.
         */
        val SSH = GitFeature(GIT_FEATURE_SSH)

        /**
         * If set, libgit2 was built with support for sub-second resolution in file
         * modification times.
         */
        val NSEC = GitFeature(GIT_FEATURE_NSEC)
    }

    override val longValue: ULong
        get() = value.toULong()

    fun enableThreads(): Boolean {
        return Threads in this
    }

    fun enableHttps(): Boolean {
        return Https in this
    }

    fun enableSSH(): Boolean {
        return SSH in this
    }

    fun enableNSEC(): Boolean {
        return NSEC in this
    }
}
