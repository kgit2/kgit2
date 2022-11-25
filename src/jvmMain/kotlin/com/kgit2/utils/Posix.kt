package com.kgit2.utils

import okio.FileSystem

actual object Posix {
    actual fun chmod(path: String, mode: Int) {
    }

    actual fun getEnv(name: String): String? {
        TODO("Not yet implemented")
    }

    actual fun setEnv(name: String, value: String) {
    }
}
