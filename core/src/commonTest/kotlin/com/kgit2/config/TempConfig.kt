package com.kgit2.config

import io.github.aakira.napier.Napier
import okio.FileSystem
import okio.Path

class TempConfig(
    var tempPath: Path,
) {
    lateinit var config: Config
    private val configList = mutableListOf<Pair<String, String>>()

    fun configs(vararg paris: Pair<String, String>) {
        Napier.d(paris.joinToString("\n"))
        configList.addAll(paris)
    }

    operator fun invoke(block: (TempConfig.() -> Unit)? = null): TempConfig {
        block?.invoke(this)
        val configPath = tempPath / ".gitconfig"
        config = Config.new()
        config.addFile(null, configPath.toString(), ConfigLevel.Highest, false)
        for ((key, value) in configList) {
            config.setString(key, value)
        }
        return this
    }
}

fun tempConfig(tempPath: Path, block: (TempConfig.() -> Unit)? = null): Config {
    return TempConfig(tempPath)(block).config
}

fun openConfig(tempPath: Path, name: String, block: (TempConfig.() -> Unit)? = null): Config {
    val configPath = tempPath / name
    FileSystem.SYSTEM.openReadWrite(configPath, mustCreate = true, mustExist = false).close()
    return Config.open(configPath.toString())
}
