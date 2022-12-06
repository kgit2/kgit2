package com.kgit2.rebase

import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.memory.RawWrapper
import com.kgit2.oid.Oid
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import libgit2.git_rebase_operation

@Raw(
    base = git_rebase_operation::class,
)
class RebaseOperation(raw: RebaseOperationRaw) : RawWrapper<git_rebase_operation, RebaseOperationRaw>(raw) {
    constructor(
        memory: Memory = Memory(),
        handler: RebaseOperationPointer = memory.alloc<git_rebase_operation>().ptr,
        initial: RebaseOperationInitial? = null
    ) : this(RebaseOperationRaw(memory, handler, initial))

    constructor(
        memory: Memory = Memory(),
        secondary: RebaseOperationSecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: RebaseOperationSecondaryInitial? = null
    ) : this(RebaseOperationRaw(memory, secondary, secondaryInitial))

    val type: RebaseType = RebaseType.from(raw.handler.pointed.type)

    val id: Oid = Oid(handler = raw.handler.pointed.id.ptr)

    val exec: String? = raw.handler.pointed.exec?.toKString()
}
