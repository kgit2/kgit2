package com.kgit2.config

import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.pointed
import kotlinx.cinterop.toKString
import libgit2.git_config_entry

@Raw(
    base = git_config_entry::class,
    free = "git_config_entry_free",
    shouldFreeOnFailure = true,
)
class ConfigEntry(raw: ConfigEntryRaw) : GitBase<git_config_entry, ConfigEntryRaw>(raw) {
    constructor(memory: Memory, handler: ConfigEntryPointer) : this(ConfigEntryRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        secondary: ConfigEntrySecondaryPointer = memory.allocPointerTo(),
        shouldFreeOnFailure: Boolean = true,
        secondaryInitial: ConfigEntrySecondaryInitial? = null,
    ) : this(ConfigEntryRaw(memory, secondary, shouldFreeOnFailure, secondaryInitial))

    /**
     * The name of the entry.
     * May be null if the name is not valid utf-8.
     */
    val name: String? = raw.handler.pointed.name?.toKString()

    val value: String? = raw.handler.pointed.value?.toKString()

    val level: ConfigLevel = ConfigLevel.fromRaw(raw.handler.pointed.level)

    val includeDepth: UInt = raw.handler.pointed.include_depth

    override fun toString(): String {
        return "ConfigEntry(name=$name, value=$value, level=$level, includeDepth=$includeDepth)"
    }
}
