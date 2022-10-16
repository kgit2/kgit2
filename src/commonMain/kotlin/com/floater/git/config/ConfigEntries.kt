package com.floater.git.config

import com.floater.git.common.error.errorCheck
import com.floater.git.model.GitBase
import kotlinx.cinterop.*
import libgit2.*

open class ConfigEntries(
    override var handler: CPointer<git_config_iterator>,
) : GitBase<CPointer<git_config_iterator>> {
    override val arena: Arena = Arena()
    open var current: ConfigEntry? = null

    /// Advances the iterator and returns the next value.
    ///
    /// Returns `None` when iteration is finished.
    open fun next(): ConfigEntry? {
        return memScoped {
            val entry = allocPointerTo<git_config_entry>()
            git_config_next(entry.ptr, handler).errorCheck()
            current = ConfigEntry(entry.value!!)
            current
        }
    }

    /// Calls the given closure for each remaining entry in the iterator.
    open fun forEach(callback: (ConfigEntry) -> Unit) {
        while (true) {
            val entry = next() ?: break
            callback(entry)
        }
    }
}
