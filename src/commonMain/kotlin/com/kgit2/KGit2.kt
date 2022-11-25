package com.kgit2

import com.kgit2.common.GitFeature
import com.kgit2.common.GitOpts
import com.kgit2.model.Version
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.atomicfu.AtomicBoolean
import kotlinx.atomicfu.atomic
import kotlinx.cinterop.*
import libgit2.*

object KGit2 {
    private val initialized: AtomicBoolean = atomic(false)

    fun initial() {
        git_libgit2_init()
        if (initialized.compareAndSet(expect = false, update = true)) {
            Platform.isCleanersLeakCheckerActive = true
            Platform.isMemoryLeakCheckerActive = true
            Napier.base(DebugAntilog())
        }
    }

    fun shutdown() {
        git_libgit2_shutdown()
    }

    fun version(): Version {
        lateinit var version: Version
        memScoped {
            val major = alloc<IntVar>()
            val minor = alloc<IntVar>()
            val patch = alloc<IntVar>()
            git_libgit2_version(major.ptr, minor.ptr, patch.ptr)
            version = Version(major.value, minor.value, patch.value)
        }
        return version
    }

    /**
     * Return the prerelease state of the libgit2 library currently being used. For nightly builds during active development, this will be "alpha". Releases may have a "beta" or release candidate ("rc1", "rc2", etc) prerelease. For a final release, this function returns NULL.
     */
    fun preRelease(): String? {
        var preRelease: String? = null
        memScoped {
            val result = git_libgit2_prerelease()
            preRelease = result?.toKString()
        }
        return preRelease
    }

    /**
     * Query compile time options for libgit2.
     *
     * @return A combination of GIT_FEATURE_* values.
     *
     * - GIT_FEATURE_THREADS
     *   Libgit2 was compiled with thread support. Note that thread support is
     *   still to be seen as a 'work in progress' - basic object lookups are
     *   believed to be threadsafe, but other operations may not be.
     *
     * - GIT_FEATURE_HTTPS
     *   Libgit2 supports the https:// protocol. This requires the openssl
     *   library to be found when compiling libgit2.
     *
     * - GIT_FEATURE_SSH
     *   Libgit2 supports the SSH protocol for network operations. This requires
     *   the libssh2 library to be found when compiling libgit2
     *
     * - GIT_FEATURE_NSEC
     *   Libgit2 supports the sub-second resolution in file modification times.
     */
    val feature: GitFeature by lazy {
        GitFeature(git_libgit2_features().convert())
    }

    object GitOptsUtil {
        fun getMWindowSize(): Int? {
            var mWindowSize: Int? = null
            memScoped {
                val mWindowSizePtr = alloc<IntVar>()
                val result = git_libgit2_opts(GitOpts.GIT_OPT_GET_MWINDOW_SIZE.ordinal, mWindowSizePtr.ptr)
                if (result == 0) {
                    mWindowSize = mWindowSizePtr.value
                }
            }
            return mWindowSize
        }

        fun setMWindowSize(size: Int): Boolean {
            return git_libgit2_opts(GitOpts.GIT_OPT_SET_MWINDOW_SIZE.ordinal, size) == 0
        }

        fun getMWindowMappedLimit(): Int? {
            var mWindowMappedLimit: Int? = null
            memScoped {
                val mWindowMappedLimitPtr = alloc<IntVar>()
                val result =
                    git_libgit2_opts(GitOpts.GIT_OPT_GET_MWINDOW_MAPPED_LIMIT.ordinal, mWindowMappedLimitPtr.ptr)
                if (result == 0) {
                    mWindowMappedLimit = mWindowMappedLimitPtr.value
                }
            }
            return mWindowMappedLimit
        }

        fun setMWindowMappedLimit(limit: Int): Boolean {
            return git_libgit2_opts(GitOpts.GIT_OPT_SET_MWINDOW_MAPPED_LIMIT.ordinal, limit) == 0
        }

        fun getMWindowFileLimit(): Int? {
            var mWindowFileLimit: Int? = null
            memScoped {
                val mWindowFileLimitPtr = alloc<IntVar>()
                val result = git_libgit2_opts(GitOpts.GIT_OPT_GET_MWINDOW_FILE_LIMIT.ordinal, mWindowFileLimitPtr.ptr)
                if (result == 0) {
                    mWindowFileLimit = mWindowFileLimitPtr.value
                }
            }
            return mWindowFileLimit
        }

        fun setMWindowFileLimit(limit: Int): Boolean {
            return git_libgit2_opts(GitOpts.GIT_OPT_SET_MWINDOW_FILE_LIMIT.ordinal, limit) == 0
        }
    }
}
