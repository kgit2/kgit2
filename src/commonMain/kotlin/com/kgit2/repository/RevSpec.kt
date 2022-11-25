package com.kgit2.repository

import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import com.kgit2.memory.Raw
import com.kgit2.`object`.Object
import kotlinx.cinterop.*
import libgit2.*

typealias RevSpecPointer = CPointer<git_revspec>

typealias RevSpecSecondaryPointer = CPointerVar<git_revspec>

typealias RevSpecInitial = RevSpecPointer.(Memory) -> Unit

class RevSpecRaw(
    memory: Memory = Memory(),
    handler: RevSpecPointer = memory.alloc<git_revspec>().ptr,
    initial: RevSpecInitial? = null,
) : Raw<git_revspec>(memory, handler.apply {
    runCatching {
        initial?.invoke(handler, memory)
    }.onFailure {
        memory.free()
    }.getOrThrow()
})

class RevSpec(raw: RevSpecRaw = RevSpecRaw()) : GitBase<git_revspec, RevSpecRaw>(raw) {
    constructor(
        memory: Memory = Memory(),
        handler: RevSpecPointer = memory.alloc<git_revspec>().ptr,
        initial: RevSpecInitial? = null,
    ) : this(RevSpecRaw(memory, handler, initial))

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
