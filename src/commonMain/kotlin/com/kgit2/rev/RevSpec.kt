package com.kgit2.rev

import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.memory.RawWrapper
import com.kgit2.`object`.Object
import com.kgit2.repository.RevspecInitial
import com.kgit2.repository.RevspecPointer
import com.kgit2.repository.RevspecRaw
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import libgit2.*

@Raw(
    base = git_revspec::class,
)
class RevSpec(raw: RevspecRaw = RevspecRaw()) : RawWrapper<git_revspec, RevspecRaw>(raw) {
    constructor(
        memory: Memory = Memory(),
        handler: RevspecPointer = memory.alloc<git_revspec>().ptr,
        initial: RevspecInitial? = null,
    ) : this(RevspecRaw(memory, handler, initial))

    val from: Object? = raw.handler.pointed.from?.let { Object(Memory(), it) }
    val to: Object? = raw.handler.pointed.to?.let { Object(Memory(), it) }
    val revParseMode: RevParseMode = RevParseMode.fromRaw(raw.handler.pointed.flags)
}

enum class RevParseMode(val value: git_revparse_mode_t) {
    Single(GIT_REVPARSE_SINGLE.convert()),
    Range(GIT_REVPARSE_RANGE.convert()),
    MergeBase(GIT_REVPARSE_MERGE_BASE.convert()),
    ;

    companion object {
        fun fromRaw(value: git_revparse_mode_t): RevParseMode {
            return when (value.toInt()) {
                GIT_REVPARSE_SINGLE -> Single
                GIT_REVPARSE_RANGE -> Range
                GIT_REVPARSE_MERGE_BASE -> MergeBase
                else -> throw IllegalArgumentException("Unknown value: $value")
            }
        }
    }
}
