package com.kgit2.config

import com.kgit2.annotations.Raw
import com.kgit2.common.error.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.IteratorBase
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.ptr
import libgit2.git_config_iterator
import libgit2.git_config_next

@Raw(
    base = git_config_iterator::class,
    free = "git_config_iterator_free",
)
class ConfigIterator(raw: ConfigIteratorRaw) : IteratorBase<git_config_iterator, ConfigIteratorRaw, ConfigEntry>(raw) {
    constructor(memory: Memory, handler: ConfigIteratorPointer) : this(ConfigIteratorRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        secondary: ConfigIteratorSecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: ConfigIteratorSecondaryInitial? = null,
    ) : this(ConfigIteratorRaw(memory, secondary = secondary, secondaryInitial = secondaryInitial))

    override fun nextRaw(): Result<ConfigEntry> = runCatching {
        ConfigEntry(shouldFreeOnFailure = false) {
            git_config_next(this.ptr, raw.handler).errorCheck()
        }
    }.onSuccess {
        it.raw.move()
    }
}
