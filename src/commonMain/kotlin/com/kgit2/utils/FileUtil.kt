package com.kgit2.utils

// import io.github.aakira.napier.Napier
import okio.FileHandle
import okio.FileSystem
import okio.Path

val tempPath = FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "kgit2"

fun <R> withTempDir(block: (Path) -> R): R = with(TempDir()) {
    println("temp path: $path")
    return try {
        block(this.path)
    } catch (e: Exception) {
        throw e
    } finally {
        clean()
    }
}

class TempDir {
    val path: Path

    init {
        val uuid = randomUUID()
        path = tempPath / uuid
        createDirectories(path, true)
    }

    fun clean() {
        deleteRecursively(path, false)
    }
}

expect fun createDirectories(path: Path, mustCreate: Boolean)

expect fun deleteRecursively(fileOrDirectory: Path, mustExist: Boolean)

expect fun openReadWrite(file: Path, mustCreate: Boolean, mustExist: Boolean): FileHandle
