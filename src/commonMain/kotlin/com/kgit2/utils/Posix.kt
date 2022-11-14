package com.kgit2.utils

expect object Posix {
    fun chmod(path: String, mode: Int = 0x1ED)
    fun getEnv(name: String): String?
    fun setEnv(name: String, value: String)
}
