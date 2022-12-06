package com.kgit2.memory

import kotlinx.cinterop.CPointed

interface RawWrapperBase<T : CPointed, R : Raw<T>> {
    val _raw: R

    val raw: R
        get() {
            // if (_raw.isFreed()) {
            //     throw IllegalStateException("This object has been freed")
            // } else {
            //     return _raw
            // }
            return _raw
        }
}
