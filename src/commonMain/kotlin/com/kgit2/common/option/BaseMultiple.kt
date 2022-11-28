package com.kgit2.common.option

abstract class BaseMultiple<T : BaseMultiple<T>> {
    abstract var longValue: ULong

    open operator fun contains(flag: T): Boolean {
        return longValue and flag.longValue == flag.longValue
    }

    // inline fun <reified T: BaseMultiple<T>> T.flag(value: ULong, on: Boolean): T {
    //     if (on) {
    //         this.longValue = this.longValue or value.longValue
    //     } else {
    //         this.longValue = this.longValue and value.longValue.inv()
    //     }
    //     return this
    // }
}

// inline infix fun <T: BaseMultiple<T>> T.or(flag: T): T {
//     return flag
// }
//
// inline infix fun <T: BaseMultiple<T>> T.and(flag: T): T {
//     return flag
// }
