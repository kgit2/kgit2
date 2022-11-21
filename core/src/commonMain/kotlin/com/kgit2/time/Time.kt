package com.kgit2.time

import com.kgit2.common.memory.Memory
import com.kgit2.memory.Raw
import com.kgit2.memory.GitBase
import kotlinx.cinterop.*
import libgit2.git_time

typealias TimeValue = CValue<git_time>

typealias TimePointer = CPointer<git_time>

typealias TimeSecondaryPointer = CPointerVar<git_time>

class TimeRaw(
    memory: Memory,
    handler: CPointer<git_time>,
) : Raw<git_time>(memory, handler) {
    constructor(memory: Memory = Memory(), value: TimeValue) : this(memory, value.getPointer(memory))

    constructor(memory: Memory, value: git_time) : this(memory, value.ptr)
}

class Time(raw: TimeRaw) : GitBase<git_time, TimeRaw>(raw) {
    constructor(memory: Memory, handler: TimePointer) : this(TimeRaw(memory, handler))

    constructor(memory: Memory, value: TimeValue) : this(TimeRaw(memory, value))

    constructor(memory: Memory, value: git_time) : this(TimeRaw(memory, value))

    constructor(memory: Memory = Memory(), seconds: Long, offset: Int) : this(memory, cValue<git_time> {
        this.time = seconds.convert()
        this.offset = offset.convert()
    })

    val seconds: Long = raw.handler.pointed.time

    val offset: Int = raw.handler.pointed.offset

    val sign: Char = when {
        offset < 0 -> '-'
        else -> '+'
    }
}
