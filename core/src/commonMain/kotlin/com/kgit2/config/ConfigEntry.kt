package com.kgit2.config

import kotlinx.cinterop.toKString
import libgit2.git_config_entry

data class ConfigEntry(
    /**
     * The name of the entry.
     * May be null if the name is not valid utf-8.
     */
    val name: String? = null,
    val value: String? = null,
    val level: ConfigLevel? = null,
    val includeDepth: UInt? = null,
) {
    companion object {
        fun fromPointer(pointer: git_config_entry): ConfigEntry {
            return ConfigEntry(
                pointer.name?.toKString(),
                pointer.value?.toKString(),
                ConfigLevel.fromRaw(pointer.level),
                pointer.include_depth
            )
        }
    }
}
