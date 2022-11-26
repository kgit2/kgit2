package com.kgit2.remote

import com.kgit2.annotations.Raw
import com.kgit2.common.error.toBoolean
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import com.kgit2.model.Oid
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import libgit2.git_remote_head

@Raw(
    base = git_remote_head::class,
)
class RemoteHead(raw: RemoteHeadRaw) : GitBase<git_remote_head, RemoteHeadRaw>(raw) {
    constructor(memory: Memory, handler: RemoteHeadPointer) : this(RemoteHeadRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        secondary: RemoteHeadSecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: RemoteHeadSecondaryInitial? = null,
    ) : this(RemoteHeadRaw(memory, secondary, secondaryInitial))

    val isLocal: Boolean = raw.handler.pointed.local.toBoolean()

    val name: String = raw.handler.pointed.name!!.toKString()

    val oid: Oid = Oid(Memory(), raw.handler.pointed.oid.ptr)

    val local: Oid = Oid(Memory(), raw.handler.pointed.loid.ptr)

    val symrefTarget: String? = raw.handler.pointed.symref_target?.toKString()
}
