package com.kgit2.config

import com.kgit2.annotations.Raw
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.IteratorBase
import com.kgit2.memory.RawWrapper
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.ptr
import libgit2.git_config_iterator
import libgit2.git_config_next
import kotlin.native.ref.WeakReference

@Raw(
    base = git_config_iterator::class,
    free = "git_config_iterator_free",
)
class ConfigIterator(raw: ConfigIteratorRaw) : RawWrapper<git_config_iterator, ConfigIteratorRaw>(raw),
    IteratorBase<ConfigEntry> {
    constructor(memory: Memory, handler: ConfigIteratorPointer) : this(ConfigIteratorRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        secondary: ConfigIteratorSecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: ConfigIteratorSecondaryInitial? = null,
    ) : this(ConfigIteratorRaw(memory, secondary = secondary, secondaryInitial = secondaryInitial))

    override var next: WeakReference<ConfigEntry>? = null

    override fun nextRaw(): Result<ConfigEntry> = runCatching {
        ConfigEntry(memory = raw.memory, shouldFreeOnFailure = false) {
            git_config_next(this.ptr, raw.handler).errorCheck()
        }
    }.onSuccess {
        it.raw.move()
    }
}
