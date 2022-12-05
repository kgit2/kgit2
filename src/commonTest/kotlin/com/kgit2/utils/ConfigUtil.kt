package com.kgit2.utils

import com.kgit2.config.Config
import okio.FileSystem
import okio.Path

class TempConfig(
    val configPath: Path,
    val config: Config = Config(configPath.toString()),
    initial: TempConfig.() -> Unit = {},
) {

    init {
        FileSystem.SYSTEM.openReadWrite(configPath, mustCreate = true, mustExist = false).close()
        this.initial()
    }

    fun string(name: String, value: String) {
        config.setString(name, value)
    }

    fun i32(name: String, value: Int) {
        config.setInt32(name, value)
    }

    fun i64(name: String, value: Long) {
        config.setInt64(name, value)
    }

    fun boolean(name: String, value: Boolean) {
        config.setBool(name, value)
    }

    fun multiVar(name: String, regexp: String, value: String) {
        config.setMultiVar(name, regexp, value)
    }
}
