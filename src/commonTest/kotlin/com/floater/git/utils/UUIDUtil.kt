@file:OptIn(ExperimentalUnsignedTypes::class)

package com.floater.git.utils

import kotlinx.cinterop.*
import platform.posix.*

fun generateUUID(): String {
    lateinit var uuidString: String
    memScoped {
        val uuid = UByteArray(16)
        uuid.usePinned { uuidPin ->
            uuid_generate(uuidPin.addressOf(0))
            val uuidBuffer = ByteArray(36)
            uuidBuffer.usePinned { uuidBufferPin ->
                uuid_unparse(uuidPin.addressOf(0), uuidBufferPin.addressOf(0))
                uuidString = uuidBuffer.decodeToString()
            }
        }
    }
    return uuidString
}

fun randomUUID(): String {
    lateinit var uuidString: String
    memScoped {
        val uuid = UByteArray(16)
        uuid.usePinned { uuidPin ->
            uuid_generate_random(uuidPin.addressOf(0))
            val uuidBuffer = ByteArray(36)
            uuidBuffer.usePinned { uuidBufferPin ->
                uuid_unparse(uuidPin.addressOf(0), uuidBufferPin.addressOf(0))
                uuidString = uuidBuffer.decodeToString()
            }
        }
    }
    return uuidString
}

fun timeUUID(): String {
    lateinit var uuidString: String
    memScoped {
        val uuid = UByteArray(16)
        uuid.usePinned { uuidPin ->
            uuid_generate_time(uuidPin.addressOf(0))
            val uuidBuffer = ByteArray(36)
            uuidBuffer.usePinned { uuidBufferPin ->
                uuid_unparse(uuidPin.addressOf(0), uuidBufferPin.addressOf(0))
                uuidString = uuidBuffer.decodeToString()
            }
        }
    }
    return uuidString
}
