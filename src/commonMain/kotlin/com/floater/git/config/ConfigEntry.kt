package com.floater.git.config

import com.floater.git.common.option.ConfigLevel
import com.floater.git.model.GitBase
import kotlinx.cinterop.*
import libgit2.git_config_entry

open class ConfigEntry(
    override var handler: CPointer<git_config_entry>,
) : GitBase<CPointer<git_config_entry>> {
    override val arena: Arena = Arena()

    /// Gets the name of this entry.
    ///
    /// May return `None` if the name is not valid utf-8
    open fun name(): String? {
        return memScoped {
            handler?.pointed?.name?.toKStringFromUtf8()
        }
    }

    /// Gets the value of this entry.
    ///
    /// May return `None` if the value is not valid utf-8
    ///
    /// # Panics
    ///
    /// Panics when no value is defined.
    open fun value(): String? {
        return memScoped {
            handler?.pointed?.value?.toKStringFromUtf8()
        }
    }


    /// Gets the configuration level of this entry.
    open fun level(): ConfigLevel? {
        return memScoped {
            handler?.pointed?.level?.let { ConfigLevel.fromValue(it) }
        }
    }

    /// Depth of includes where this variable was found
    open fun includeDepth(): UInt? {
        return memScoped {
            handler?.pointed?.include_depth
        }
    }
}
