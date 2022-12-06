package com.kgit2.config

import libgit2.GIT_CONFIG_HIGHEST_LEVEL
import libgit2.GIT_CONFIG_LEVEL_APP
import libgit2.GIT_CONFIG_LEVEL_GLOBAL
import libgit2.GIT_CONFIG_LEVEL_LOCAL
import libgit2.GIT_CONFIG_LEVEL_PROGRAMDATA
import libgit2.GIT_CONFIG_LEVEL_SYSTEM
import libgit2.GIT_CONFIG_LEVEL_XDG
import libgit2.git_config_level_t

enum class ConfigLevel(val value: git_config_level_t) {
    /** System-wide on Windows, for compatibility with portable git */
    ProgramData(GIT_CONFIG_LEVEL_PROGRAMDATA),

    /** System-wide configuration file; /etc/gitconfig on Linux systems */
    System(GIT_CONFIG_LEVEL_SYSTEM),

    /** XDG compatible configuration file; typically ~/.config/git/config */
    XDG(GIT_CONFIG_LEVEL_XDG),

    /** User-specific configuration file (also called Global configuration
     * file); typically ~/.gitconfig
     */
    Global(GIT_CONFIG_LEVEL_GLOBAL),

    /** Repository specific configuration file; $WORK_DIR/.git/config on
     * non-bare repos
     */
    Local(GIT_CONFIG_LEVEL_LOCAL),

    /** Application specific configuration file; freely defined by applications
     */
    App(GIT_CONFIG_LEVEL_APP),

    /** Represents the highest level available config file (i.e. the most
     * specific config file available that actually is loaded)
     */
    Highest(GIT_CONFIG_HIGHEST_LEVEL);

    companion object {
        fun fromRaw(value: git_config_level_t): ConfigLevel {
            return when (value) {
                ProgramData.value -> ProgramData
                System.value -> System
                XDG.value -> XDG
                Global.value -> Global
                Local.value -> Local
                App.value -> App
                Highest.value -> Highest
                else -> error("Unknown ConfigLevel value: $value")
            }
        }
    }
}
