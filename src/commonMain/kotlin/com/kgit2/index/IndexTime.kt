package com.kgit2.index

import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import com.kgit2.memory.Raw
import kotlinx.cinterop.cValue
import kotlinx.cinterop.pointed
import libgit2.git_index_time

// class IndexTimeRaw(
//     memory: Memory = Memory(),
// ) : Raw<git_index_time>(memory, cValue<git_index_time> {
//     this.seconds = second
//     this.nanoseconds = nanosecond
// }.getPointer(memory)) {
//     operator fun compareTo(other: IndexTimeRaw): Int {
//         return when {
//             second > other.second -> 1
//             second < other.second -> -1
//             else -> {
//                 when {
//                     nanosecond > other.nanosecond -> 1
//                     nanosecond < other.nanosecond -> -1
//                     else -> 0
//                 }
//             }
//         }
//     }
// }
//
// class IndexTime(
//     second: Int,
//     nanosecond: UInt,
// ) : GitBase<git_index_time, IndexTimeRaw>(IndexTimeRaw(second, nanosecond)) {
//     val second = raw.handler.pointed.seconds
//
//     val nanosecond = raw.handler.pointed.nanoseconds
// }
