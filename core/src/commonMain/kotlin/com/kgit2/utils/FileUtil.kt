package com.kgit2.utils

import com.kgit2.KGit2
import io.github.aakira.napier.Napier
import okio.FileHandle
import okio.FileSystem
import okio.Path

val tempPath = FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "kgit2"

fun <R> withTempDir(block: (Path) -> R): R {
    val uuid = randomUUID()
    val path = tempPath / uuid
    Napier.d("repo path: $path")
    return try {
        createDirectories(path, false)
        block(path)
    } catch (e: Exception) {
        throw e
    } finally {
        deleteRecursively(path, false)
    }
}

expect fun createDirectories(path: Path, mustCreate: Boolean)

expect fun deleteRecursively(fileOrDirectory: Path, mustExist: Boolean)

expect fun openReadWrite(file: Path, mustCreate: Boolean, mustExist: Boolean): FileHandle
