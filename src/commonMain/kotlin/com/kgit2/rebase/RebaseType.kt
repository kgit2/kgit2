package com.kgit2.rebase

enum class RebaseType(val value: UInt) {
    None(0U),
    Apply(1U),
    Merge(2U),
    Interactive(3U),
    ;

    companion object {
        fun from(value: UInt): RebaseType {
            return when (value) {
                0U -> None
                1U -> Apply
                2U -> Merge
                3U -> Interactive
                else -> throw IllegalArgumentException("Unknown value: $value")
            }
        }
    }
}
