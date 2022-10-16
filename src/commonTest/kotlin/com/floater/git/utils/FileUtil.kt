package com.floater.git.utils

import kotlinx.cinterop.*
import okio.FileSystem
import platform.posix.uuid_generate_random
import platform.posix.uuid_unparse

val tempPath = FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "kgit2"

fun withTempDir(block: (String) -> Unit) {
    val uuid = randomUUID()
    val path = tempPath / uuid
    println("repo path: $path")
    try {
        FileSystem.SYSTEM.createDirectories(path, false)
        block(path.toString())
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        FileSystem.SYSTEM.deleteRecursively(path, false)
    }
}
