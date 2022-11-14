package com.kgit2.common.option

import com.kgit2.reference.ReferenceFormat

abstract class BaseMultiple<T : BaseMultiple<T>>() {
    abstract val longValue: ULong

    open operator fun contains(flag: T): Boolean {
        return longValue and flag.longValue == flag.longValue
    }
}

inline infix fun <T: BaseMultiple<T>> T.or(flag: T): T {
    return flag
}
