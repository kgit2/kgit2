package com.kgit2.common.option.mutually

import platform.posix.O_RDONLY
import platform.posix.O_RDWR
import platform.posix.O_WRONLY

enum class FileOpenFlags(val value: Int) {
    /// Open the file for reading.
    ReadOnly(O_RDONLY),

    /// Open the file for writing.
    WriteOnly(O_WRONLY),

    /// Open the file for appending.
    ReadWrite(O_RDWR);

    companion object {
        fun fromRaw(value: Int): FileOpenFlags {
            return when (value) {
                O_RDONLY -> ReadOnly
                O_WRONLY -> WriteOnly
                O_RDWR -> ReadWrite
                else -> error("Unknown file open flags: $value")
            }
        }
    }
}
