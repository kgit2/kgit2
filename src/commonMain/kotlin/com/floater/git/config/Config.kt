package com.floater.git.config

import cnames.structs.git_config
import com.floater.git.common.error.errorCheck
import com.floater.git.common.option.ConfigLevel
import com.floater.git.model.GitBase
import com.floater.git.model.GitBuf
import com.floater.git.repository.Repository
import kotlinx.cinterop.*
import libgit2.*

open class Config(
    override var handler: CPointer<git_config> = memScoped {
        val pointer = allocPointerTo<git_config>()
        git_config_new(pointer.ptr).errorCheck()
        pointer.value!!
    }
) : GitBase<CPointer<git_config>> {
    override val arena: Arena = Arena()

    companion object {
        fun open(path: String): Config {
            return memScoped {
                val pointer = allocPointerTo<git_config>()
                git_config_open_ondisk(pointer.ptr, path).errorCheck()
                Config(pointer.value!!)
            }
        }

        fun openDefault(): Config {
            return memScoped {
                val pointer = allocPointerTo<git_config>()
                git_config_open_default(pointer.ptr).errorCheck()
                Config(pointer.value!!)
            }
        }

        fun openGlobal(): Config {
            return memScoped {
                val pointer = allocPointerTo<git_config>()
                git_config_open_global(pointer.ptr, pointer.value).errorCheck()
                Config(pointer.value!!)
            }
        }

        fun openLevel(level: ConfigLevel): Config {
            return memScoped {
                val pointer = allocPointerTo<git_config>()
                git_config_open_level(pointer.ptr, pointer.value, level.value).errorCheck()
                Config(pointer.value!!)
            }
        }

        fun findGlobal(): String {
            return memScoped {
                val buf = GitBuf(this@memScoped)
                git_config_find_global(buf.handler).errorCheck()
                buf.ptr!!
            }
        }

        fun findSystem(): String {
            return memScoped {
                val buf = GitBuf(this@memScoped)
                git_config_find_system(buf.handler).errorCheck()
                buf.ptr!!
            }
        }

        fun findXDG(): String {
            return memScoped {
                val buf = GitBuf(this@memScoped)
                git_config_find_xdg(buf.handler).errorCheck()
                buf.ptr!!
            }
        }

        fun findProgramdata(): String {
            return memScoped {
                val buf = GitBuf(this@memScoped)
                git_config_find_programdata(buf.handler).errorCheck()
                buf.ptr!!
            }
        }
    }

    fun addFile(repository: Repository?, path: String?, level: ConfigLevel, force: Boolean = false) {
        git_config_add_file_ondisk(handler, path, level.value, repository?.handler, if (force) 1 else 0).errorCheck()
    }

    fun setBool(name: String, value: Boolean) {
        if (name.isEmpty()) return
        git_config_set_bool(handler, name, if (value) 1 else 0).errorCheck()
    }

    fun getBool(name: String): Boolean? {
        if (name.isEmpty()) return null
        return memScoped {
            val out = alloc<IntVar>()
            git_config_get_bool(out.ptr, handler, name).errorCheck()
            out.value != 0
        }
    }

    fun parseBool(value: String): Boolean? {
        if (value.isEmpty()) return null
        return memScoped {
            val out = alloc<IntVar>()
            git_config_parse_bool(out.ptr, value).errorCheck()
            out.value != 0
        }
    }

    fun setInt(name: String, value: Int) {
        if (name.isEmpty()) return
        git_config_set_int32(handler, name, value).errorCheck()
    }

    fun parseInt(value: String): Int {
        return memScoped {
            val out = alloc<IntVar>()
            git_config_parse_int32(out.ptr, value).errorCheck()
            out.value
        }
    }

    fun getInt(name: String): Int {
        return memScoped {
            val out = alloc<IntVar>()
            git_config_get_int32(out.ptr, handler, name).errorCheck()
            out.value
        }
    }

    fun setInt64(name: String, value: Long) {
        if (name.isEmpty()) return
        git_config_set_int64(handler, name, value).errorCheck()
    }

    fun getInt64(name: String): Long? {
        if (name.isEmpty()) return null
        return memScoped {
            val out = alloc<LongVar>()
            git_config_get_int64(out.ptr, handler, name).errorCheck()
            out.value
        }
    }

    fun parseInt64(value: String): Long? {
        if (value.isEmpty()) return null
        return memScoped {
            val out = alloc<LongVar>()
            git_config_parse_int64(out.ptr, value).errorCheck()
            out.value
        }
    }

    fun setString(name: String, value: String) {
        if (value.isEmpty() || value.isEmpty()) return
        git_config_set_string(handler, name, value).errorCheck()
    }

    fun getString(name: String): String? {
        if (name.isEmpty()) return null
        return memScoped {
            val out = allocPointerTo<ByteVar>()
            git_config_get_string(out.ptr, handler, name).errorCheck()
            out.value?.toKString()
        }
    }

    fun getStringBuf(name: String): String? {
        if (name.isEmpty()) return null
        return memScoped {
            val buf = GitBuf(this@memScoped)
            git_config_get_string_buf(buf.handler, handler, name).errorCheck()
            buf.ptr
        }
    }

    fun getPath(name: String): String? {
        if (name.isEmpty()) return null
        return memScoped {
            val buf = GitBuf(this@memScoped)
            git_config_get_path(buf.handler, handler, name).errorCheck()
            buf.ptr!!
        }
    }

    fun parsePath(value: String): String? {
        if (value.isEmpty()) return null
        return memScoped {
            val buf = GitBuf(this@memScoped)
            git_config_parse_path(buf.handler, value).errorCheck()
            buf.ptr!!
        }
    }

    fun setMultiVar(name: String, regexp: String, value: String) {
        if (name.isEmpty() || value.isEmpty() || regexp.isEmpty()) return
        git_config_set_multivar(handler, name, regexp, value).errorCheck()
    }

    fun deleteMultiVar(name: String?, regexp: String?) {
        if (name == null || regexp == null) return
        git_config_delete_multivar(handler, name, regexp).errorCheck()
    }

    fun getMultiVar(name: String, regexp: String): ConfigEntries {
        return memScoped {
            val entries = allocPointerTo<git_config_iterator>()
            git_config_multivar_iterator_new(entries.ptr, handler, name, regexp).errorCheck()
            ConfigEntries(entries.value!!)
        }
    }

    fun getEntry(name: String): ConfigEntry {
        return memScoped {
            val entry = allocPointerTo<git_config_entry>()
            git_config_get_entry(entry.ptr, handler, name).errorCheck()
            ConfigEntry(entry.value!!)
        }
    }

    fun getEntries(glob: String?): ConfigEntries {
        return memScoped {
            val entries = allocPointerTo<git_config_iterator>()
            if (!glob.isNullOrEmpty()) {
                git_config_iterator_glob_new(entries.ptr, handler, glob).errorCheck()
            } else {
                git_config_iterator_new(entries.ptr, handler).errorCheck()
            }
            ConfigEntries(entries.value!!)
        }
    }

    fun deleteEntry(name: String?) {
        git_config_delete_entry(handler, name).errorCheck()
    }

    fun snapshot(): Config {
        return memScoped {
            val pointer = allocPointerTo<git_config>()
            git_config_snapshot(pointer.ptr, handler).errorCheck()
            Config(pointer.value!!)
        }
    }
}
