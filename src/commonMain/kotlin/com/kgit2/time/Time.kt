package com.kgit2.time

import com.kgit2.model.GitBase
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.cValue
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import libgit2.git_time

data class Time(
    val seconds: Long,
    val offset: Int,
    override val handler: CPointer<git_time>,
) : GitBase<CPointer<git_time>> {
    val sign: Char = when {
        offset < 0 -> '-'
        else -> '+'
    }

    companion object {
        fun new(seconds: Long, offset: Int): Time {
            return Time(seconds, offset, memScoped {
                cValue<git_time> {
                    this.time = seconds
                    this.offset = offset
                    this.sign = when {
                        offset < 0 -> '-'.code.toByte()
                        else -> '+'.code.toByte()
                    }
                }.ptr
            })
        }

        fun fromHandler(handler: CPointer<git_time>): Time = Time(handler.pointed.time, handler.pointed.offset, handler)
    }
}
