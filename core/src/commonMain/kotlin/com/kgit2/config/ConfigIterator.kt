package com.kgit2.config

import com.kgit2.common.error.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.BeforeFree
import com.kgit2.memory.IteratorBase
import com.kgit2.memory.Raw
import kotlinx.cinterop.*
import libgit2.git_config_iterator
import libgit2.git_config_iterator_free
import libgit2.git_config_next

typealias ConfigIteratorPointer = CPointer<git_config_iterator>

typealias ConfigIteratorSecondaryPointer = CPointerVar<git_config_iterator>

typealias ConfigIteratorInitial = ConfigIteratorSecondaryPointer.(Memory) -> Unit

class ConfigIteratorRaw(
    memory: Memory,
    handler: ConfigIteratorPointer,
) : Raw<git_config_iterator>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: ConfigIteratorSecondaryPointer = memory.allocPointerTo(),
        initializer: ConfigIteratorInitial? = null,
    ) : this(memory, handler.apply {
        runCatching {
            initializer?.invoke(this, memory)
        }.onFailure {
            git_config_iterator_free(handler.value)
            memory.free()
        }.getOrThrow()
    }.value!!)

    override val beforeFree: BeforeFree = {
        git_config_iterator_free(handler)
    }
}

class ConfigIterator(raw: ConfigIteratorRaw) : IteratorBase<git_config_iterator, ConfigIteratorRaw, ConfigEntry>(raw) {
    constructor(memory: Memory, handler: ConfigIteratorPointer) : this(ConfigIteratorRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: ConfigIteratorSecondaryPointer = memory.allocPointerTo(),
        initializer: ConfigIteratorInitial? = null,
    ) : this(ConfigIteratorRaw(memory, handler, initializer))

    override fun nextRaw(): Result<ConfigEntry> = runCatching {
        ConfigEntry(shouldFreeOnFailure = false) {
            git_config_next(this.ptr, raw.handler).errorCheck()
        }
    }.onSuccess {
        it.raw.move()
    }
}
