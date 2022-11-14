package com.kgit2.config

import cnames.structs.git_config
import cnames.structs.git_config_iterator
import com.kgit2.common.error.errorCheck
import com.kgit2.common.error.toBoolean
import com.kgit2.common.error.toInt
import com.kgit2.model.AutoFreeGitBase
import com.kgit2.model.autoFreeScoped
import com.kgit2.model.toKString
import com.kgit2.model.withGitBuf
import com.kgit2.repository.Repository
import kotlinx.cinterop.*
import libgit2.*

open class Config(
    override val handler: CPointer<git_config>,
    override val arena: Arena,
) : AutoFreeGitBase<CPointer<git_config>> {
    companion object {
        fun new(): Config {
            val arena = Arena()
            val pointer = arena.allocPointerTo<git_config>()
            git_config_new(pointer.ptr).errorCheck()
            return Config(pointer.value!!, arena)
        }

        fun open(path: String): Config {
            val arena = Arena()
            val pointer = arena.allocPointerTo<git_config>()
            git_config_open_ondisk(pointer.ptr, path).errorCheck()
            return Config(pointer.value!!, arena)
        }

        fun openDefault(): Config {
            val arena = Arena()
            val pointer = arena.allocPointerTo<git_config>()
            git_config_open_default(pointer.ptr).errorCheck()
            return Config(pointer.value!!, arena)
        }

        fun openGlobal(): Config {
            val arena = Arena()
            val pointer = arena.allocPointerTo<git_config>()
            git_config_open_global(pointer.ptr, pointer.value).errorCheck()
            return Config(pointer.value!!, arena)
        }

        fun openLevel(level: ConfigLevel): Config {
            val arena = Arena()
            val pointer = arena.allocPointerTo<git_config>()
            git_config_open_level(pointer.ptr, pointer.value, level.value).errorCheck()
            return Config(pointer.value!!, arena)
        }

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
            return memScoped {
                val out = alloc<IntVar>()
                runCatching {
                    git_config_parse_int32(out.ptr, value).errorCheck()
                }.getOrNull()?.let { out.value }
            }
        }

        fun parseInt64(value: String): Long? {
            if (value.isEmpty()) return null
            return memScoped {
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
            return memScoped {
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

    override fun free() {
        git_config_free(handler)
        super.free()
    }

    fun addFile(repository: Repository?, path: String?, level: ConfigLevel, force: Boolean = false) {
        git_config_add_file_ondisk(handler, path, level.value, repository?.handler, if (force) 1 else 0).errorCheck()
    }

    fun removeEntry(name: String) {
        git_config_delete_entry(handler, name).errorCheck()
    }

    fun removeMultiVar(name: String, regexp: String) {
        git_config_delete_multivar(handler, name, regexp).errorCheck()
    }

    fun setBool(name: String, value: Boolean) {
        if (name.isEmpty()) throw IllegalArgumentException("name is empty")
        git_config_set_bool(handler, name, value.toInt()).errorCheck()
    }

    fun getBool(name: String): Boolean {
        if (name.isEmpty()) throw IllegalArgumentException("name is empty")
        return autoFreeScoped {
            val out = alloc<IntVar>()
            git_config_get_bool(out.ptr, handler, name).errorCheck()
            out.value.toBoolean()
        }
    }

    fun setInt32(name: String, value: Int) {
        if (name.isEmpty()) throw IllegalArgumentException("name is empty")
        git_config_set_int32(handler, name, value).errorCheck()
    }

    fun getInt32(name: String): Int {
        if (name.isEmpty()) throw IllegalArgumentException("name is empty")
        return autoFreeScoped {
            val out = alloc<IntVar>()
            git_config_get_int32(out.ptr, handler, name).errorCheck()
            out.value
        }
    }

    fun setInt64(name: String, value: Long) {
        if (name.isEmpty()) throw IllegalArgumentException("name is empty")
        git_config_set_int64(handler, name, value).errorCheck()
    }

    fun getInt64(name: String): Long {
        if (name.isEmpty()) throw IllegalArgumentException("name is empty")
        return autoFreeScoped {
            val out = alloc<LongVar>()
            git_config_get_int64(out.ptr, handler, name).errorCheck()
            out.value
        }
    }

    fun setString(name: String, value: String) {
        // if (value.isEmpty() || value.isEmpty()) throw IllegalArgumentException("name or value is empty")
        git_config_set_string(handler, name, value).errorCheck()
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
        return autoFreeScoped {
            val out = allocPointerTo<ByteVar>()
            git_config_get_string(out.ptr, handler, name).errorCheck()
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
            git_config_get_string_buf(buf, handler, name).errorCheck()
            buf.toKString()!!
        }
    }

    fun getPath(name: String): String? {
        if (name.isEmpty()) return null
        return runCatching {
            withGitBuf { buf ->
                git_config_get_path(buf, handler, name).errorCheck()
                buf.toKString()
            }
        }.getOrNull()
    }

    fun setMultiVar(name: String, regexp: String, value: String) {
        if (name.isEmpty() || value.isEmpty() || regexp.isEmpty()) return
        git_config_set_multivar(handler, name, regexp, value).errorCheck()
    }

    fun deleteMultiVar(name: String?, regexp: String?) {
        if (name == null || regexp == null) return
        git_config_delete_multivar(handler, name, regexp).errorCheck()
    }

    fun getMultiVar(name: String, regexp: String? = null): List<ConfigEntry> {
        return entryList(name = name, regexp = regexp)
    }

    fun getEntry(name: String): ConfigEntry {
        return autoFreeScoped {
            val entry = allocPointerTo<git_config_entry>()
            git_config_get_entry(entry.ptr, handler, name).errorCheck()
            val configEntry = ConfigEntry.fromPointer(entry.pointed!!)
            git_config_entry_free(entry.value)
            configEntry
        }
    }

    fun getEntries(glob: String? = null): List<ConfigEntry> {
        return entryList(glob)
    }

    private fun entryList(
        glob: String? = null,
        name: String? = null,
        regexp: String? = null,
    ): MutableList<ConfigEntry> {
        val entries = mutableListOf<ConfigEntry>()
        autoFreeScoped {
            val pointer = allocPointerTo<git_config_iterator>()
            when {
                !glob.isNullOrEmpty() -> git_config_iterator_glob_new(pointer.ptr, handler, glob).errorCheck()
                !name.isNullOrEmpty() -> git_config_multivar_iterator_new(
                    pointer.ptr,
                    handler,
                    name,
                    regexp
                ).errorCheck()

                else -> git_config_iterator_new(pointer.ptr, handler).errorCheck()
            }
            val iterator = pointer.value!!
            try {
                while (true) {
                    val entry = allocPointerTo<git_config_entry>()
                    when (val errorCode = git_config_next(entry.ptr, iterator)) {
                        GIT_ITEROVER -> break
                        else -> errorCode.errorCheck()
                    }
                    entries.add(ConfigEntry.fromPointer(entry.pointed!!))
                }
            } finally {
                git_config_iterator_free(iterator)
            }
        }
        return entries
    }

    fun deleteEntry(name: String?) {
        git_config_delete_entry(handler, name).errorCheck()
    }

    fun snapshot(): Config {
        val arena = Arena()
        val pointer = arena.allocPointerTo<git_config>()
        git_config_snapshot(pointer.ptr, handler).errorCheck()
        return Config(pointer.value!!, arena)
    }
}
