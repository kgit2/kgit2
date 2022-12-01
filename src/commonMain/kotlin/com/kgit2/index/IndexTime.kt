package com.kgit2.index

import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.memory.RawWrapper
import kotlinx.cinterop.cValue
import kotlinx.cinterop.pointed
import libgit2.git_index_time

@Raw(
    base = git_index_time::class,
)
class IndexTime(raw: IndexTimeRaw) : RawWrapper<git_index_time, IndexTimeRaw>(raw) {
    constructor(memory: Memory, raw: git_index_time) : this(IndexTimeRaw(memory, raw))

    constructor(memory: Memory, value: IndexTimeValue) : this(IndexTimeRaw(memory, value))

    constructor(seconds: Int, nanoseconds: UInt) : this(Memory(), cValue<git_index_time> {
        this.seconds = seconds
        this.nanoseconds = nanoseconds
    })

    val seconds = raw.handler.pointed.seconds

    val nanoseconds = raw.handler.pointed.nanoseconds

    operator fun compareTo(other: IndexTime): Int {
        return when {
            seconds > other.seconds -> 1
            seconds < other.seconds -> -1
            else -> {
                when {
                    nanoseconds > other.nanoseconds -> 1
                    nanoseconds < other.nanoseconds -> -1
                    else -> 0
                }
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (null === other) return false
        if (this::class != other::class) return false

        other as IndexTime

        if (seconds != other.seconds) return false
        if (nanoseconds != other.nanoseconds) return false

        return true
    }

    override fun hashCode(): Int {
        var result = seconds.hashCode()
        result = 31 * result + nanoseconds.hashCode()
        return result
    }
}
