package com.kgit2.config

import com.kgit2.common.memory.Memory
import com.kgit2.memory.BeforeFree
import com.kgit2.memory.GitBase
import com.kgit2.memory.Raw
import io.github.aakira.napier.Napier
import kotlinx.cinterop.*
import libgit2.git_config_entry
import libgit2.git_config_entry_free

typealias ConfigEntryPointer = CPointer<git_config_entry>

typealias ConfigEntrySecondaryPointer = CPointerVar<git_config_entry>

typealias ConfigEntryInitial = ConfigEntrySecondaryPointer.(Memory) -> Unit

class ConfigEntryRaw(
    memory: Memory,
    handler: ConfigEntryPointer,
) : Raw<git_config_entry>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: ConfigEntrySecondaryPointer = memory.allocPointerTo(),
        shouldFreeOnFailure: Boolean = true,
        initializer: ConfigEntryInitial? = null,
    ) : this(memory, handler.apply {
        runCatching {
            initializer?.invoke(this, memory)
        }.onFailure {
            if (shouldFreeOnFailure) {
                git_config_entry_free(handler.value)
            }
            memory.free()
        }.getOrThrow()
    }.value!!)

    override val beforeFree: BeforeFree = {
        git_config_entry_free(handler)
    }
}

class ConfigEntry(raw: ConfigEntryRaw) : GitBase<git_config_entry, ConfigEntryRaw>(raw) {
    constructor(memory: Memory, handler: ConfigEntryPointer) : this(ConfigEntryRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: ConfigEntrySecondaryPointer = memory.allocPointerTo(),
        shouldFreeOnFailure: Boolean = true,
        initializer: ConfigEntryInitial? = null,
    ) : this(ConfigEntryRaw(memory, handler, shouldFreeOnFailure, initializer))

    /**
     * The name of the entry.
     * May be null if the name is not valid utf-8.
     */
    val name: String? = raw.handler.pointed.name?.toKString()

    val value: String? = raw.handler.pointed.value?.toKString()

    val level: ConfigLevel = ConfigLevel.fromRaw(raw.handler.pointed.level)

    val includeDepth: UInt = raw.handler.pointed.include_depth
}
