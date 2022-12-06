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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Time) return false

        if (seconds != other.seconds) return false
        if (offset != other.offset) return false
        if (sign != other.sign) return false

        return true
    }

    override fun hashCode(): Int {
        var result = seconds.hashCode()
        result = 31 * result + offset.hashCode()
        result = 31 * result + sign.hashCode()
        return result
    }

    override fun toString(): String {
        return "Time(seconds=$seconds, offset=$offset, sign=$sign)"
    }
}
