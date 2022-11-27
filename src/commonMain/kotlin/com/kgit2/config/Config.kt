package com.kgit2.config

import cnames.structs.git_config
import com.kgit2.annotations.Raw
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.extend.toBoolean
import com.kgit2.common.extend.toInt
import com.kgit2.common.memory.Memory
import com.kgit2.common.memory.memoryScoped
import com.kgit2.memory.GitBase
import com.kgit2.model.toKString
import com.kgit2.model.withGitBuf
import com.kgit2.repository.Repository
import kotlinx.cinterop.*
import libgit2.*

@Raw(
    base = git_config::class,
    free = "git_config_free",
)
class Config(raw: ConfigRaw) : GitBase<git_config, ConfigRaw>(raw) {
    constructor(memory: Memory, handler: ConfigPointer) : this(ConfigRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        secondary: ConfigSecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: ConfigSecondaryInitial?,
    ) : this(ConfigRaw(memory, secondary, secondaryInitial))

    constructor(path: String) : this(secondaryInitial = {
        git_config_open_ondisk(this.ptr, path).errorCheck()
    })

    constructor(default: Boolean = false) : this(secondaryInitial = {
        when (default) {
            true -> git_config_open_default(this.ptr).errorCheck()
            false -> git_config_new(this.ptr).errorCheck()
        }
    })

    fun openGlobal(): Config = Config {
        git_config_open_global(this.ptr, raw.handler).errorCheck()
    }

    fun openLevel(level: ConfigLevel): Config = Config {
        git_config_open_level(this.ptr, raw.handler, level.value).errorCheck()
    }

    companion object {
        fun findGlobal(): String = withGitBuf { buf ->
            git_config_find_global(buf).errorCheck()
            buf.toKString()!!
        }

        fun findSystem(): String = withGitBuf { buf ->
            git_config_find_system(buf).errorCheck()
            buf.toKString()!!
        }

        fun findXDG(): String = withGitBuf { buf ->
            git_config_find_xdg(buf).errorCheck()
            buf.toKString()!!
        }

        fun findProgramdata(): String = withGitBuf { buf ->
            git_config_find_programdata(buf).errorCheck()
            buf.toKString()!!
        }

        fun parseInt32(value: String): Int? {
            return memoryScoped {
                val out = alloc<IntVar>()
                runCatching {
                    git_config_parse_int32(out.ptr, value).errorCheck()
                }.getOrNull()?.let { out.value }
            }
        }

        fun parseInt64(value: String): Long? {
            if (value.isEmpty()) return null
            return memoryScoped {
                val out = alloc<LongVar>()
                runCatching {
                    git_config_parse_int64(out.ptr, value).errorCheck()
                }.getOrNull()?.let { out.value }
            }
        }

        /**
         * Parse a string as a bool.
         *
         * Interprets "true", "yes", "on", 1, or any non-zero number as true.
         * Interprets "false", "no", "off", 0, or an empty string as false.
         */
        fun parseBool(value: String): Boolean? {
            return memoryScoped {
                val out = alloc<IntVar>()
                runCatching {
                    git_config_parse_bool(out.ptr, value).errorCheck()
                }.getOrNull()?.let { out.value != 0 }
            }
        }

        fun parsePath(value: String): String? {
            if (value.isEmpty()) return null
            return runCatching {
                withGitBuf { buf ->
                    git_config_parse_path(buf, value).errorCheck()
                    buf.toKString()
                }
            }.getOrNull()
        }
    }

    fun addFile(repository: Repository?, path: String?, level: ConfigLevel, force: Boolean = false) {
        git_config_add_file_ondisk(raw.handler, path, level.value, repository?.raw?.handler, force.toInt()).errorCheck()
    }

    fun removeEntry(name: String) {
        git_config_delete_entry(raw.handler, name).errorCheck()
    }

    fun removeMultiVar(name: String, regexp: String) {
        git_config_delete_multivar(raw.handler, name, regexp).errorCheck()
    }

    fun setBool(name: String, value: Boolean) {
        if (name.isEmpty()) throw IllegalArgumentException("name is empty")
        git_config_set_bool(raw.handler, name, value.toInt()).errorCheck()
    }

    fun getBool(name: String): Boolean {
        if (name.isEmpty()) throw IllegalArgumentException("name is empty")
        return memoryScoped {
            val out = alloc<IntVar>()
            git_config_get_bool(out.ptr, raw.handler, name).errorCheck()
            out.value.toBoolean()
        }
    }

    fun setInt32(name: String, value: Int) {
        if (name.isEmpty()) throw IllegalArgumentException("name is empty")
        git_config_set_int32(raw.handler, name, value).errorCheck()
    }

    fun getInt32(name: String): Int {
        if (name.isEmpty()) throw IllegalArgumentException("name is empty")
        return memoryScoped {
            val out = alloc<IntVar>()
            git_config_get_int32(out.ptr, raw.handler, name).errorCheck()
            out.value
        }
    }

    fun setInt64(name: String, value: Long) {
        if (name.isEmpty()) throw IllegalArgumentException("name is empty")
        git_config_set_int64(raw.handler, name, value).errorCheck()
    }

    fun getInt64(name: String): Long {
        if (name.isEmpty()) throw IllegalArgumentException("name is empty")
        return memoryScoped {
            val out = alloc<LongVar>()
            git_config_get_int64(out.ptr, raw.handler, name).errorCheck()
            out.value
        }
    }

    fun setString(name: String, value: String) {
        git_config_set_string(raw.handler, name, value).errorCheck()
    }

    /**
     * Get the value of a string config variable.
     *
     * This function can only be used on snapshot config objects. The
     * string is owned by the config and should not be freed by the
     * user. The pointer will be valid until the config is freed.
     *
     * All config files will be looked into, in the order of their
     * defined level. A higher level means a higher priority. The
     * first occurrence of the variable will be returned here.
     */
    fun getString(name: String): String {
        if (name.isEmpty()) throw IllegalArgumentException("name is empty")
        return memoryScoped {
            val out = allocPointerTo<ByteVar>()
            git_config_get_string(out.ptr, raw.handler, name).errorCheck()
            out.value!!.toKString()
        }
    }

    /**
     * Get the value of a string config variable.
     *
     * The value of the config will be copied into the buffer.
     *
     * All config files will be looked into, in the order of their
     * defined level. A higher level means a higher priority. The
     * first occurrence of the variable will be returned here.
     */
    fun getStringBuf(name: String): String {
        if (name.isEmpty()) throw IllegalArgumentException("name is empty")
        return withGitBuf { buf ->
            git_config_get_string_buf(buf, raw.handler, name).errorCheck()
            buf.toKString()!!
        }
    }

    fun getPath(name: String): String? {
        if (name.isEmpty()) return null
        return runCatching {
            withGitBuf { buf ->
                git_config_get_path(buf, raw.handler, name).errorCheck()
                buf.toKString()
            }
        }.getOrNull()
    }

    fun setMultiVar(name: String, regexp: String, value: String) {
        if (name.isEmpty() || value.isEmpty() || regexp.isEmpty()) return
        git_config_set_multivar(raw.handler, name, regexp, value).errorCheck()
    }

    fun deleteMultiVar(name: String?, regexp: String?) {
        if (name == null || regexp == null) return
        git_config_delete_multivar(raw.handler, name, regexp).errorCheck()
    }

    fun getMultiVar(name: String, regexp: String? = null): ConfigIterator = ConfigIterator {
        git_config_multivar_iterator_new(this.ptr, raw.handler, name, regexp).errorCheck()
    }

    fun getEntry(name: String): ConfigEntry = ConfigEntry {
        git_config_get_entry(this.ptr, raw.handler, name).errorCheck()
    }

    fun getEntries(glob: String? = null): ConfigIterator = ConfigIterator {
        when {
            glob != null -> git_config_iterator_glob_new(this.ptr, raw.handler, glob)
            else -> git_config_iterator_new(this.ptr, raw.handler)
        }.errorCheck()
    }

    fun deleteEntry(name: String?) {
        git_config_delete_entry(raw.handler, name).errorCheck()
    }

    fun snapshot(): Config = Config {
        git_config_snapshot(this.ptr, raw.handler).errorCheck()
    }
}
