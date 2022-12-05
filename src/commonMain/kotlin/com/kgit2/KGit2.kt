@file:Suppress("PropertyName", "MemberVisibilityCanBePrivate", "FunctionName")
package com.kgit2

import com.kgit2.common.GitFeature
import com.kgit2.common.GitOptions
import com.kgit2.common.extend.toBoolean
import com.kgit2.common.extend.toInt
import com.kgit2.common.memory.memoryScoped
import com.kgit2.config.ConfigLevel
import com.kgit2.model.Buf
import com.kgit2.model.StrArray
import com.kgit2.model.Version
import com.kgit2.`object`.ObjectType
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.plugins.UserAgent
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
        memoryScoped {
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
        memoryScoped {
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
     * - [GitFeature.enableThreads]
     *   Libgit2 was compiled with thread support. Note that thread support is
     *   still to be seen as a 'work in progress' - basic object lookups are
     *   believed to be threadsafe, but other operations may not be.
     *
     * - [GitFeature.enableHttps]
     *   Libgit2 supports the https:// protocol. This requires the openssl
     *   library to be found when compiling libgit2.
     *
     * - [GitFeature.enableSSH]
     *   Libgit2 supports the SSH protocol for network operations. This requires
     *   the libssh2 library to be found when compiling libgit2
     *
     * - [GitFeature.enableNSEC]
     *   Libgit2 supports the sub-second resolution in file modification times.
     */
    val feature: GitFeature by lazy {
        GitFeature(git_libgit2_features().convert())
    }

    object Options {
        data class CachedMemoryMode(val current: Long, val allowed: Long)
        val CachedMemory: CachedMemoryMode
            get() = memoryScoped {
                val current = alloc<LongVar>()
                val allowed = alloc<LongVar>()
                git_libgit2_opts(GitOptions.CachedMemory.setter.value.convert(), current.ptr, allowed.ptr)
                CachedMemoryMode(current.value, allowed.value)
            }

        var WindowSize: Int
            get() = memoryScoped {
                val result = alloc<IntVar>()
                git_libgit2_opts(GitOptions.WindowSize.getter!!.value.convert(), result.ptr)
                result.value
            }
            set(value) {
                git_libgit2_opts(GitOptions.WindowSize.setter.value.convert(), value)
            }

        var WindowMappedLimit: Int
            get() = memoryScoped {
                val result = alloc<IntVar>()
                git_libgit2_opts(GitOptions.WindowMappedLimit.getter!!.value.convert(), result.ptr)
                result.value
            }
            set(value) {
                git_libgit2_opts(GitOptions.WindowMappedLimit.setter.value.convert(), value)
            }

        var TemplatePath: String
            get() = with(Buf {
                git_libgit2_opts(GitOptions.TemplatePath.getter!!.value.convert(), this)
            }) {
                this.buffer!!.toKString()
            }
            set(value) = memoryScoped {
                git_libgit2_opts(GitOptions.TemplatePath.setter.value.convert(), value.cstr.ptr)
            }

        var UserAgent: String
            get() = with(Buf {
                git_libgit2_opts(GitOptions.UserAgent.getter!!.value.convert(), this)
            }) {
                this.buffer!!.toKString()
            }
            set(value) = memoryScoped {
                git_libgit2_opts(GitOptions.UserAgent.setter.value.convert(), value.cstr.ptr)
            }

        var WindowsShareMode: ULong
            get() = memoryScoped {
                val result = alloc<ULongVar>()
                git_libgit2_opts(GitOptions.WindowsShareMode.getter!!.value.convert(), result.ptr)
                result.value
            }
            set(value) {
                git_libgit2_opts(GitOptions.WindowsShareMode.setter.value.convert(), value)
            }

        var PackMaxObjects: ULong
            get() = memoryScoped {
                val result = alloc<ULongVar>()
                git_libgit2_opts(GitOptions.PackMaxObjects.getter!!.value.convert(), result.ptr)
                result.value
            }
            set(value) {
                git_libgit2_opts(GitOptions.PackMaxObjects.setter.value.convert(), value)
            }

        var WindowFileLimit: ULong
            get() = memoryScoped {
                val result = alloc<ULongVar>()
                git_libgit2_opts(GitOptions.WindowFileLimit.getter!!.value.convert(), result.ptr)
                result.value
            }
            set(value) {
                git_libgit2_opts(GitOptions.WindowFileLimit.setter.value.convert(), value)
            }

        var Extensions: List<String>
            get() = with(StrArray {
                git_libgit2_opts(GitOptions.Extensions.getter!!.value.convert(), this)
            }) {
                this.innerList.toList()
            }
            set(value) = memoryScoped {
                git_libgit2_opts(GitOptions.Extensions.setter.value.convert(), value.map { it.cstr.ptr }.toCValues().ptr)
            }

        var OwnerValidation: Int
            get() = memoryScoped {
                val result = alloc<IntVar>()
                git_libgit2_opts(GitOptions.OwnerValidation.getter!!.value.convert(), result.ptr)
                result.value
            }
            set(value) {
                git_libgit2_opts(GitOptions.OwnerValidation.setter.value.convert(), value)
            }

        fun GetSearchPath(level: ConfigLevel): Buf = Buf {
            git_libgit2_opts(GitOptions.SearchPath.getter!!.value.convert(), level.value, this)
        }

        fun SetSearchPath(level: ConfigLevel, path: String) = memoryScoped {
            git_libgit2_opts(GitOptions.SearchPath.setter.value.convert(), level.value, path.cstr.ptr)
        }

        fun SetCacheObjectLimit(type: ObjectType, size: ULong) {
            git_libgit2_opts(GitOptions.CacheObjectLimit.setter.value.convert(), type.value, size)
        }

        fun SetCacheMaxSize(size: Long) {
            git_libgit2_opts(GitOptions.CacheMaxSize.setter.value.convert(), size)
        }

        /**
         * Set the SSL certificate-authority locations.
         * @param file is the location of a file containing several certificates concatenated together.
         * @param path is the location of a directory holding several
         * certificates, one per file.
         * Either parameter may be `NULL`, but not both.
         */
        fun SetSSLCertLocations(file: String?, path: String?) = memoryScoped {
            git_libgit2_opts(GitOptions.SSLCertLocations.setter.value.convert(), file?.cstr?.ptr, path?.cstr?.ptr)
        }

        fun SetSSLCiphers(ciphers: String) = memoryScoped {
            git_libgit2_opts(GitOptions.SSLCiphers.setter.value.convert(), ciphers.cstr.ptr)
        }

        fun SetOdbPackedPriority(priority: Int) {
            git_libgit2_opts(GitOptions.OdbPackedPriority.setter.value.convert(), priority)
        }

        fun SetOdbLoosePriority(priority: Int) {
            git_libgit2_opts(GitOptions.OdbLoosePriority.setter.value.convert(), priority)
        }

        fun EnableCaching(enable: Boolean) {
            git_libgit2_opts(GitOptions.EnableCaching.setter.value.convert(), enable.toInt())
        }

        fun EnableStrictObjectCreation(enable: Boolean) {
            git_libgit2_opts(GitOptions.EnableStrictObjectCreation.setter.value.convert(), enable.toInt())
        }

        fun EnableStrictREFCreation(enable: Boolean) {
            git_libgit2_opts(GitOptions.EnableStrictREFCreation.setter.value.convert(), enable.toInt())
        }

        fun EnableOFSDelta(enable: Boolean) {
            git_libgit2_opts(GitOptions.EnableOFSDelta.setter.value.convert(), enable.toInt())
        }

        fun EnableFSyncGitDir(enable: Boolean) {
            git_libgit2_opts(GitOptions.EnableFSyncGitDir.setter.value.convert(), enable.toInt())
        }

        fun EnableStrictHashVerification(enable: Boolean) {
            git_libgit2_opts(GitOptions.EnableStrictHashVerification.setter.value.convert(), enable.toInt())
        }

        fun EnableUnsavedIndexSafety(enable: Boolean) {
            git_libgit2_opts(GitOptions.EnableUnsavedIndexSafety.setter.value.convert(), enable.toInt())
        }

        fun DisablePackKeepFileChecks(enable: Boolean) {
            git_libgit2_opts(GitOptions.DisablePackKeepFileChecks.setter.value.convert(), enable.toInt())
        }

        fun EnableHttpExpectContinue(enable: Boolean) {
            git_libgit2_opts(GitOptions.EnableHttpExpectContinue.setter.value.convert(), enable.toInt())
        }
    }
}
