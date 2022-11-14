package com.kgit2.utils

import okio.FileSystem
import okio.Path

val tempPath = FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "kgit2"

fun <R> withTempDir(block: (Path) -> R): R {
    val uuid = randomUUID()
    val path = tempPath / uuid
    println("repo path: $path")
    return try {
        FileSystem.SYSTEM.createDirectories(path, false)
        block(path)
    } catch (e: Exception) {
        throw e
    } finally {
        FileSystem.SYSTEM.deleteRecursively(path, false)
    }
}
