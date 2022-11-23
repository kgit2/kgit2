package com.kgit2.memory

import kotlinx.cinterop.CPointed
import kotlin.native.internal.Cleaner
import kotlin.native.internal.createCleaner

abstract class GitBase<T: CPointed, R: Raw<T>>(
    protected val _raw: R
) {
    val raw: R = _raw
        get() {
            if (field.isFreed()) {
                throw IllegalStateException("This object has been freed")
            } else {
                return field
            }
        }

    open val cleaner: Cleaner = createCleaner(raw) { it.free() }
}
