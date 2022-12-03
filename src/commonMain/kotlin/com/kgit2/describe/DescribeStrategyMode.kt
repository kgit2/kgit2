package com.kgit2.describe

import libgit2.*

enum class DescribeStrategyMode(val value: git_describe_strategy_t) {
    Default(git_describe_strategy_t.GIT_DESCRIBE_DEFAULT),
    Tags(git_describe_strategy_t.GIT_DESCRIBE_TAGS),
    All(git_describe_strategy_t.GIT_DESCRIBE_ALL),
    ;

    companion object {
        fun from(value: git_describe_strategy_t): DescribeStrategyMode {
            return when (value) {
                git_describe_strategy_t.GIT_DESCRIBE_DEFAULT -> Default
                git_describe_strategy_t.GIT_DESCRIBE_TAGS -> Tags
                git_describe_strategy_t.GIT_DESCRIBE_ALL -> All
                else -> throw IllegalArgumentException("Unknown value: $value")
            }
        }

        fun fromUInt(value: UInt): DescribeStrategyMode {
            return when (value) {
                git_describe_strategy_t.GIT_DESCRIBE_DEFAULT.value -> Default
                git_describe_strategy_t.GIT_DESCRIBE_TAGS.value -> Tags
                git_describe_strategy_t.GIT_DESCRIBE_ALL.value -> All
                else -> throw IllegalArgumentException("Unknown value: $value")
            }
        }
    }
}
