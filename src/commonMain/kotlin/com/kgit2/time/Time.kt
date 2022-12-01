package com.kgit2.time

import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.memory.RawWrapper
import kotlinx.cinterop.cValue
import kotlinx.cinterop.convert
import kotlinx.cinterop.pointed
import libgit2.git_time

@Raw(
    base = git_time::class,
)
class Time(raw: TimeRaw) : RawWrapper<git_time, TimeRaw>(raw) {
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
