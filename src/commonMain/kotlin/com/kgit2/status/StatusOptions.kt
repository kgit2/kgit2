package com.kgit2.status

import com.kgit2.common.error.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import com.kgit2.memory.Raw
import kotlinx.cinterop.*
import libgit2.GIT_STATUS_OPTIONS_VERSION
import libgit2.git_status_options
import libgit2.git_status_options_init

typealias StatusOptionsPointer = CPointer<git_status_options>

// typealias StatusOptionsSecondaryPointer = CPointerVar<git_status_options>

typealias StatusOptionsInitial = StatusOptionsPointer.(Memory) -> Unit

class StatusOptionsRaw(
    memory: Memory = Memory(),
    handler: StatusOptionsPointer = memory.alloc<git_status_options>().ptr,
    initial: StatusOptionsInitial? = null,
) : Raw<git_status_options>(memory, handler.apply {
    runCatching {
        initial?.invoke(handler, memory)
    }.onFailure {
        memory.free()
    }.getOrThrow()
}) {
    internal val pathspec: MutableList<String> = mutableListOf()
    internal val pinned = pathspec.pin()

    override val beforeFree: () -> Unit = {
        pinned.unpin()
    }
}

class StatusOptions(raw: StatusOptionsRaw) : GitBase<git_status_options, StatusOptionsRaw>(raw) {
    constructor(
        memory: Memory = Memory(),
        handler: StatusOptionsPointer = memory.alloc<git_status_options>().ptr,
        initial: StatusOptionsInitial? = null,
    ) : this(StatusOptionsRaw(memory, handler, initial))

    constructor() : this(initial = {
        git_status_options_init(this, GIT_STATUS_OPTIONS_VERSION).errorCheck()
    })

    var show: StatusShow = StatusShow.fromRaw(raw.handler.pointed.show)
        set(value) {
            field = value
            raw.handler.pointed.show = value.value
        }

    var flags: StatusOptionsFlag = StatusOptionsFlag(raw.handler.pointed.flags)
        set(value) {
            field = value
            raw.handler.pointed.flags = value.value
        }

    var renameThreshold: UShort = raw.handler.pointed.rename_threshold
        set(value) {
            field = value
            raw.handler.pointed.rename_threshold = value
        }

    fun pathspec(vararg pathspec: String) {
        raw.pathspec.addAll(pathspec)
        raw.handler.pointed.pathspec.count = raw.pinned.get().size.convert()
        raw.handler.pointed.pathspec.strings = raw.pinned.get().toCStringArray(raw.memory)
    }

    fun flag(flag: StatusOptionsFlag, on: Boolean) {
        flags = when (on) {
            true -> flags or flag
            false -> flags and !flag
        }
    }
}
