package com.kgit2.time

import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import kotlinx.cinterop.*
import libgit2.git_time

@Raw(
    base = git_time::class,
)
class Time(raw: TimeRaw) : GitBase<git_time, TimeRaw>(raw) {
    constructor(memory: Memory, value: git_time) : this(TimeRaw(memory, value))

    constructor(memory: Memory, value: TimeValue) : this(TimeRaw(memory, value))

    constructor(memory: Memory, handler: TimePointer) : this(TimeRaw(memory, handler))

    constructor(seconds: Long, offset: Int) : this(Memory(), cValue<git_time> {
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
