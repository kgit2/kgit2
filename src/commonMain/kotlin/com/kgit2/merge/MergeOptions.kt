package com.kgit2.merge

import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.diff.DiffSimilarityMetric
import com.kgit2.memory.RawWrapper
import kotlinx.cinterop.cstr
import kotlinx.cinterop.pointed
import kotlinx.cinterop.toKString
import libgit2.GIT_MERGE_OPTIONS_VERSION
import libgit2.git_merge_options
import libgit2.git_merge_options_init

@Raw(
    base = git_merge_options::class,
)
class MergeOptions(
    raw: MergeOptionsRaw = MergeOptionsRaw(initial = {
        git_merge_options_init(this, GIT_MERGE_OPTIONS_VERSION)
    })
) : RawWrapper<git_merge_options, MergeOptionsRaw>(raw) {
    constructor(memory: Memory = Memory(), handler: MergeOptionsPointer) : this(MergeOptionsRaw(memory, handler))

    val flags: MergeFlag = MergeFlag(raw.handler.pointed.flags) {
        raw.handler.pointed.flags = it
    }

    var renameThreshold: UInt = raw.handler.pointed.rename_threshold
        set(value) {
            field = value
            raw.handler.pointed.rename_threshold = value
        }

    var targetLimit: UInt = raw.handler.pointed.target_limit
        set(value) {
            field = value
            raw.handler.pointed.target_limit = value
        }

    /**
     * Pluggable similarity metric; pass NULL to use internal metric
     */
    val metric: DiffSimilarityMetric? = null

    var recursionLimit: UInt = raw.handler.pointed.recursion_limit
        set(value) {
            field = value
            raw.handler.pointed.recursion_limit = value
        }

    var defaultDriver: String? = raw.handler.pointed.default_driver?.toKString()
        set(value) {
            field = value
            raw.handler.pointed.default_driver = value?.cstr?.getPointer(raw.memory)
        }

    val fileFavor: MergeFileFavor = MergeFileFavor(raw.handler.pointed.file_favor) {
        raw.handler.pointed.file_favor = it
    }

    val fileFlags: MergeFileFlag = MergeFileFlag(raw.handler.pointed.file_flags) {
        raw.handler.pointed.file_flags = it
    }
}
