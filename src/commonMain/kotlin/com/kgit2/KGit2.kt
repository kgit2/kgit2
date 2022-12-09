@file:Suppress("PropertyName", "MemberVisibilityCanBePrivate", "FunctionName")
package com.kgit2

import com.kgit2.common.GitFeature
import com.kgit2.common.GitOptions
import com.kgit2.common.extend.toInt
import com.kgit2.common.memory.memoryScoped
import com.kgit2.config.ConfigLevel
import com.kgit2.model.Buf
import com.kgit2.model.Version
import com.kgit2.`object`.ObjectType
// import io.github.aakira.napier.DebugAntilog
// import io.github.aakira.napier.Napier
import kotlinx.atomicfu.AtomicBoolean
import kotlinx.atomicfu.atomic
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.LongVar
import kotlinx.cinterop.ULongVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.cstr
import kotlinx.cinterop.get
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toCValues
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import libgit2.git_libgit2_features
import libgit2.git_libgit2_init
import libgit2.git_libgit2_opts
import libgit2.git_libgit2_prerelease
import libgit2.git_libgit2_shutdown
import libgit2.git_libgit2_version
import libgit2.git_strarray
import libgit2.git_strarray_dispose

object KGit2 {
    private val initialized: AtomicBoolean = atomic(false)

    fun initial() {
        git_libgit2_init()
        if (initialized.compareAndSet(expect = false, update = true)) {
            Platform.isCleanersLeakCheckerActive = true
            Platform.isMemoryLeakCheckerActive = true
            // Napier.base(DebugAntilog())
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
        val cachedMemory: CachedMemoryMode
            get() = memoryScoped {
                val current = alloc<LongVar>()
                val allowed = alloc<LongVar>()
                git_libgit2_opts(GitOptions.CachedMemory.setter.value.convert(), current.ptr, allowed.ptr)
                CachedMemoryMode(current.value, allowed.value)
            }

        var windowSize: Int
            get() = memoryScoped {
                val result = alloc<IntVar>()
                git_libgit2_opts(GitOptions.WindowSize.getter!!.value.convert(), result.ptr)
                result.value
            }
            set(value) {
                git_libgit2_opts(GitOptions.WindowSize.setter.value.convert(), value)
            }

        var windowMappedLimit: Int
            get() = memoryScoped {
                val result = alloc<IntVar>()
                git_libgit2_opts(GitOptions.WindowMappedLimit.getter!!.value.convert(), result.ptr)
                result.value
            }
            set(value) {
                git_libgit2_opts(GitOptions.WindowMappedLimit.setter.value.convert(), value)
            }

        var templatePath: String
            get() = with(Buf {
                git_libgit2_opts(GitOptions.TemplatePath.getter!!.value.convert(), this)
            }) {
                this.buffer!!.toKString()
            }
            set(value) = memoryScoped {
                git_libgit2_opts(GitOptions.TemplatePath.setter.value.convert(), value.cstr.ptr)
            }

        var userAgent: String
            get() = with(Buf {
                git_libgit2_opts(GitOptions.UserAgent.getter!!.value.convert(), this)
            }) {
                this.buffer!!.toKString()
            }
            set(value) = memoryScoped {
                git_libgit2_opts(GitOptions.UserAgent.setter.value.convert(), value.cstr.ptr)
            }

        var windowsShareMode: ULong
            get() = memoryScoped {
                val result = alloc<ULongVar>()
                git_libgit2_opts(GitOptions.WindowsShareMode.getter!!.value.convert(), result.ptr)
                result.value
            }
            set(value) {
                git_libgit2_opts(GitOptions.WindowsShareMode.setter.value.convert(), value)
            }

        var packMaxObjects: ULong
            get() = memoryScoped {
                val result = alloc<ULongVar>()
                git_libgit2_opts(GitOptions.PackMaxObjects.getter!!.value.convert(), result.ptr)
                result.value
            }
            set(value) {
                git_libgit2_opts(GitOptions.PackMaxObjects.setter.value.convert(), value)
            }

        var windowFileLimit: ULong
            get() = memoryScoped {
                val result = alloc<ULongVar>()
                git_libgit2_opts(GitOptions.WindowFileLimit.getter!!.value.convert(), result.ptr)
                result.value
            }
            set(value) {
                git_libgit2_opts(GitOptions.WindowFileLimit.setter.value.convert(), value)
            }

        val extensions: List<String>
            get() = memoryScoped {
                val strArray = alloc<git_strarray>()
                git_libgit2_opts(GitOptions.Extensions.getter!!.value.convert(), strArray.ptr)
                val result = List(strArray.count.toInt()) {
                    strArray.strings!![it]!!.toKString()
                }
                git_strarray_dispose(strArray.ptr)
                result
            }

        var ownerValidation: Int
            get() = memoryScoped {
                val result = alloc<IntVar>()
                git_libgit2_opts(GitOptions.OwnerValidation.getter!!.value.convert(), result.ptr)
                result.value
            }
            set(value) {
                git_libgit2_opts(GitOptions.OwnerValidation.setter.value.convert(), value)
            }

        fun addExtensions(extension: Collection<String>) = memoryScoped {
            git_libgit2_opts(GitOptions.Extensions.setter.value.convert(), extension.map { it.cstr.ptr }.toCValues().ptr, extension.size)
        }

        fun getSearchPath(level: ConfigLevel): Buf = Buf {
            git_libgit2_opts(GitOptions.SearchPath.getter!!.value.convert(), level.value, this)
        }

        fun setSearchPath(level: ConfigLevel, path: String) = memoryScoped {
            git_libgit2_opts(GitOptions.SearchPath.setter.value.convert(), level.value, path)
        }

        fun resetSearchPath(level: ConfigLevel) = memoryScoped {
            git_libgit2_opts(GitOptions.SearchPath.setter.value.convert(), level.value, null)
        }

        fun setCacheObjectLimit(type: ObjectType, size: ULong) {
            git_libgit2_opts(GitOptions.CacheObjectLimit.setter.value.convert(), type.value, size)
        }

        fun setCacheMaxSize(size: Long) {
            git_libgit2_opts(GitOptions.CacheMaxSize.setter.value.convert(), size)
        }

        /**
         * Set the SSL certificate-authority locations.
         * @param file is the location of a file containing several certificates concatenated together.
         * @param path is the location of a directory holding several
         * certificates, one per file.
         * Either parameter may be `NULL`, but not both.
         */
        fun setSSLCertLocations(file: String?, path: String?) = memoryScoped {
            git_libgit2_opts(GitOptions.SSLCertLocations.setter.value.convert(), file?.cstr?.ptr, path?.cstr?.ptr)
        }

        fun setSSLCiphers(ciphers: String) = memoryScoped {
            git_libgit2_opts(GitOptions.SSLCiphers.setter.value.convert(), ciphers.cstr.ptr)
        }

        fun setOdbPackedPriority(priority: Int) {
            git_libgit2_opts(GitOptions.OdbPackedPriority.setter.value.convert(), priority)
        }

        fun setOdbLoosePriority(priority: Int) {
            git_libgit2_opts(GitOptions.OdbLoosePriority.setter.value.convert(), priority)
        }

        fun caching(enable: Boolean) {
            git_libgit2_opts(GitOptions.EnableCaching.setter.value.convert(), enable.toInt())
        }

        fun strictObjectCreation(enable: Boolean) {
            git_libgit2_opts(GitOptions.EnableStrictObjectCreation.setter.value.convert(), enable.toInt())
        }

        fun strictREFCreation(enable: Boolean) {
            git_libgit2_opts(GitOptions.EnableStrictREFCreation.setter.value.convert(), enable.toInt())
        }

        fun ofsDelta(enable: Boolean) {
            git_libgit2_opts(GitOptions.EnableOFSDelta.setter.value.convert(), enable.toInt())
        }

        fun fsyncGitDir(enable: Boolean) {
            git_libgit2_opts(GitOptions.EnableFSyncGitDir.setter.value.convert(), enable.toInt())
        }

        fun strictHashVerification(enable: Boolean) {
            git_libgit2_opts(GitOptions.EnableStrictHashVerification.setter.value.convert(), enable.toInt())
        }

        fun unsavedIndexSafety(enable: Boolean) {
            git_libgit2_opts(GitOptions.EnableUnsavedIndexSafety.setter.value.convert(), enable.toInt())
        }

        fun packKeepFileChecks(enable: Boolean) {
            git_libgit2_opts(GitOptions.DisablePackKeepFileChecks.setter.value.convert(), enable.toInt())
        }

        fun httpExpectContinue(enable: Boolean) {
            git_libgit2_opts(GitOptions.EnableHttpExpectContinue.setter.value.convert(), enable.toInt())
        }
    }
}
