package com.floater.git

import com.floater.git.common.GitFeature
import com.floater.git.common.GitOpts
import com.floater.git.common.error.errorCheck
import com.floater.git.common.option.GitRepositoryOpenFlag
import com.floater.git.common.option.RepositoryInitOptions
import com.floater.git.model.GitBuf
import com.floater.git.model.Version
import com.floater.git.repository.Repository
import kotlinx.cinterop.*
import libgit2.*

object KGit2 {
    init {
        git_libgit2_init()
    }

    fun initRepository(path: String): Repository {
        assert(path.isNotEmpty())
        val handler = memScoped {
            val pointer = allocPointerTo<git_repository>()
            git_repository_init(pointer.ptr, path, 0U).errorCheck()
            pointer.value!!
        }
        return Repository(handler, path)
    }

    fun initBare(path: String): Repository {
        assert(path.isNotEmpty())
        val handler = memScoped {
            val pointer = allocPointerTo<git_repository>()
            git_repository_init(pointer.ptr, path, 1U).errorCheck()
            pointer.value!!
        }
        return Repository(handler, path)
    }

    fun initOpts(path: String, opts: RepositoryInitOptions): Repository {
        assert(path.isNotEmpty())
        val handler = memScoped {
            val pointer = allocPointerTo<git_repository>()
            git_repository_init_ext(pointer.ptr, path, opts.toRaw(this)).errorCheck()
            pointer.value!!
        }
        return Repository(handler, path)
    }

    fun open(path: String): Repository {
        assert(path.isNotEmpty())
        val handler = memScoped {
            val pointer = allocPointerTo<git_repository>()
            git_repository_open(pointer.ptr, path).errorCheck()
            pointer.value!!
        }
        return Repository(handler, path)
    }

    fun openBare(path: String): Repository {
        assert(path.isNotEmpty())
        val handler = memScoped {
            val pointer = allocPointerTo<git_repository>()
            git_repository_open_bare(pointer.ptr, path).errorCheck()
            pointer.value!!
        }
        return Repository(handler, path)
    }

    fun openFromEnv(path: String): Repository {
        assert(path.isNotEmpty())
        val handler = memScoped {
            val pointer = allocPointerTo<git_repository>()
            git_repository_open_ext(pointer.ptr, path, GIT_REPOSITORY_OPEN_FROM_ENV, null).errorCheck()
            pointer.value!!
        }
        return Repository(handler, path)
    }

    fun openExt(path: String, flags: GitRepositoryOpenFlag, ceilingDirs: String? = null): Repository {
        assert(path.isNotEmpty())
        val handler = memScoped {
            val pointer = allocPointerTo<git_repository>()
            git_repository_open_ext(pointer.ptr, path, flags.value, ceilingDirs).errorCheck()
            pointer.value!!
        }
        return Repository(handler, path)
    }

    fun openFromWorktree(path: String): Repository {
        assert(path.isNotEmpty())
        val handler = memScoped {
            val pointer = allocPointerTo<git_repository>()
            // TODO()
//            git_repository_open_from_worktree(pointer.ptr, path).errorCheck()
            pointer.value!!
        }
        return Repository(handler, path)
    }

    fun discover(path: String): Repository {
        return memScoped {
            val buf = GitBuf(this@memScoped)
            git_repository_discover(buf.handler, path, 1, null).errorCheck()
            open(buf.ptr!!)
        }
    }

    // TODO()
    /*fun clone(url: String, localPath: String, bare: Boolean = false): Repository {
        handler = memScoped {
            val pointer = allocPointerTo<git_repository>()
            pointer.value!!
        }
        return this
    }*/

    fun shutDown() {
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
    fun features(): Int {
        return git_libgit2_features()
    }

    fun enableThreadsFeature(): Boolean {
        return this.features().and(GitFeature.GIT_FEATURE_THREADS.value) == GitFeature.GIT_FEATURE_THREADS.value
    }

    fun enableHttpsFeature(): Boolean {
        return this.features().and(GitFeature.GIT_FEATURE_HTTPS.value) == GitFeature.GIT_FEATURE_HTTPS.value
    }

    fun enableSSHFeature(): Boolean {
        return this.features().and(GitFeature.GIT_FEATURE_SSH.value) == GitFeature.GIT_FEATURE_SSH.value
    }

    fun enableNSecFeature(): Boolean {
        return this.features().and(GitFeature.GIT_FEATURE_NSEC.value) == GitFeature.GIT_FEATURE_NSEC.value
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
