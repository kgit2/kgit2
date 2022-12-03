package com.kgit2.describe

import com.kgit2.annotations.Raw
import com.kgit2.common.extend.toBoolean
import com.kgit2.common.extend.toInt
import com.kgit2.memory.RawWrapper
import kotlinx.cinterop.cstr
import kotlinx.cinterop.pointed
import kotlinx.cinterop.toKString
import libgit2.GIT_DESCRIBE_OPTIONS_VERSION
import libgit2.git_describe_options
import libgit2.git_describe_options_init

@Raw(
    base = git_describe_options::class
)
class DescribeOptions(
    raw: DescribeOptionsRaw = DescribeOptionsRaw(initial = {
        git_describe_options_init(this, GIT_DESCRIBE_OPTIONS_VERSION)
    }),
) : RawWrapper<git_describe_options, DescribeOptionsRaw>(raw) {
    var maxCandidatesTags: UInt = raw.handler.pointed.max_candidates_tags
        set(value) {
            field = value
            raw.handler.pointed.max_candidates_tags = value
        }

    var describeStrategy: DescribeStrategyMode = DescribeStrategyMode.fromUInt(raw.handler.pointed.describe_strategy)
        set(value) {
            field = value
            raw.handler.pointed.describe_strategy = value.value.value
        }

    var pattern: String? = raw.handler.pointed.pattern?.toKString()
        set(value) {
            field = value
            raw.handler.pointed.pattern = value?.cstr?.getPointer(raw.memory)
        }

    var onlyFollowFirstParent: Boolean = raw.handler.pointed.only_follow_first_parent.toBoolean()
        set(value) {
            field = value
            raw.handler.pointed.only_follow_first_parent = value.toInt()
        }

    var showCommitOidAsFallback: Boolean = raw.handler.pointed.show_commit_oid_as_fallback.toBoolean()
        set(value) {
            field = value
            raw.handler.pointed.show_commit_oid_as_fallback = value.toInt()
        }
}
