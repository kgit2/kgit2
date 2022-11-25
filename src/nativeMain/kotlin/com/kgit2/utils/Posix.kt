package com.kgit2.utils

import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import platform.posix.getenv
import platform.posix.setenv

actual object Posix {
    actual fun chmod(path: String, mode: Int) {
        platform.posix.chmod(path, mode.convert())
    }

    actual fun getEnv(name: String): String? {
        return memScoped {
            val env = getenv(name)
            env?.toKString()
        }
    }

    actual fun setEnv(name: String, value: String) {
        setenv(name, value, 1)
    }
}
