package com.floater.git.common.option

import libgit2.GIT_CONFIG_LEVEL_XDG
import libgit2.git_config_level_t

enum class ConfigLevel(val value: git_config_level_t) {
    /** System-wide on Windows, for compatibility with portable git */
    GIT_CONFIG_LEVEL_PROGRAMDATA(1),

    /** System-wide configuration file; /etc/gitconfig on Linux systems */
    GIT_CONFIG_LEVEL_SYSTEM(2),

    /** XDG compatible configuration file; typically ~/.config/git/config */
    GIT_CONFIG_LEVEL_XDG(3),

    /** User-specific configuration file (also called Global configuration
     * file); typically ~/.gitconfig
     */
    GIT_CONFIG_LEVEL_GLOBAL(4),

    /** Repository specific configuration file; $WORK_DIR/.git/config on
     * non-bare repos
     */
    GIT_CONFIG_LEVEL_LOCAL(5),

    /** Application specific configuration file; freely defined by applications
     */
    GIT_CONFIG_LEVEL_APP(6),

    /** Represents the highest level available config file (i.e. the most
     * specific config file available that actually is loaded)
     */
    GIT_CONFIG_HIGHEST_LEVEL(-1);

    companion object {
        fun fromValue(value: git_config_level_t): ConfigLevel {
            return when (value) {
                GIT_CONFIG_LEVEL_PROGRAMDATA.value -> GIT_CONFIG_LEVEL_PROGRAMDATA
                GIT_CONFIG_LEVEL_SYSTEM.value -> GIT_CONFIG_LEVEL_SYSTEM
                GIT_CONFIG_LEVEL_XDG.value -> GIT_CONFIG_LEVEL_XDG
                GIT_CONFIG_LEVEL_GLOBAL.value -> GIT_CONFIG_LEVEL_GLOBAL
                GIT_CONFIG_LEVEL_LOCAL.value -> GIT_CONFIG_LEVEL_LOCAL
                GIT_CONFIG_LEVEL_APP.value -> GIT_CONFIG_LEVEL_APP
                GIT_CONFIG_HIGHEST_LEVEL.value -> GIT_CONFIG_HIGHEST_LEVEL
                else -> throw IllegalArgumentException("Unknown ConfigLevel value: $value")
            }
        }
    }
}
