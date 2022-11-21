package com.kgit2.memory

import io.github.aakira.napier.Napier
import kotlinx.cinterop.CPointed
import kotlinx.cinterop.CPointer
import kotlin.native.internal.Cleaner
import kotlin.native.internal.createCleaner

abstract class GitBase<T: CPointed, Raw: Binding<T>>(
    protected val _raw: Raw
) {
    val raw: Raw = _raw
        get() {
            if (field.freed.value) {
                throw IllegalStateException("This object has been freed")
            } else {
                return field
            }
        }

    open val cleaner: Cleaner = createCleaner(raw) { it.free() }

    // fun raw(): Raw {
    //     if (this.raw.freed.value) {
    //         throw IllegalStateException("This object has been freed")
    //     } else {
    //         return this.raw
    //     }
    // }
}
