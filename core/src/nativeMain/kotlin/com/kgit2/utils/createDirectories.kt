package com.kgit2.utils

import okio.FileHandle
import okio.FileSystem
import okio.Path

actual fun createDirectories(path: Path, mustCreate: Boolean) {
    FileSystem.SYSTEM.createDirectories(path, mustCreate)
}

actual fun deleteRecursively(fileOrDirectory: Path, mustExist: Boolean) {
    FileSystem.SYSTEM.deleteRecursively(fileOrDirectory, mustExist)
}

actual fun openReadWrite(file: Path, mustCreate: Boolean, mustExist: Boolean): FileHandle {
    return FileSystem.SYSTEM.openReadWrite(file, mustCreate, mustExist)
}
