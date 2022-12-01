package com.kgit2.memory

import kotlinx.cinterop.CPointed
import kotlin.native.internal.Cleaner
import kotlin.native.internal.createCleaner

abstract class RawWrapper<T : CPointed, R : Raw<T>> (
    override val _raw: R,
) : CleanAble, RawWrapperBase<T, R> {
    final override val raw: R
        get() = super.raw

    override val cleaner: Cleaner = createCleaner(raw) { it.free() }
}
