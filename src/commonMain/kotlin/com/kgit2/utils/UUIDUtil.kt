@file:OptIn(ExperimentalUnsignedTypes::class)

package com.kgit2.utils

import kotlinx.cinterop.*
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.random.Random

/**
 * generate a random uuid-v4 with pure kotlin
 */
fun randomUUID(): String {
    val uuid = ByteArray(18)
    val random = Random.Default
    random.nextBytes(uuid)

    // variant
    uuid[8] = uuid[8] and 0x3f or 0x80.toByte()
    // version
    uuid[6] = uuid[6] and 0x0f or 0x40.toByte()
    val hexDigits = "0123456789abcdef"
    val result = CharArray(36)
    var i = 0
    for (b in uuid) {
        result[i++] = hexDigits[b.toInt() shr 4 and 0xf]
        result[i++] = hexDigits[b.toInt() and 0xf]
    }
    result[8] = '-'
    result[13] = '-'
    result[18] = '-'
    result[23] = '-'
    return result.concatToString()
}
