package com.kgit2.remote

import com.kgit2.model.AutoFreeGitBase
import com.kgit2.model.Oid
import kotlinx.cinterop.*
import libgit2.git_remote_head

class RemoteHead(
    override val handler: CPointer<git_remote_head>,
    override val arena: Arena,
) : AutoFreeGitBase<CPointer<git_remote_head>> {
    val isLocal: Boolean = handler.pointed.local != 0

    val name: String = handler.pointed.name!!.toKString()

    val oid: Oid = Oid(handler.pointed.oid.ptr, arena)

    val local: Oid = Oid(handler.pointed.loid.ptr, arena)

    val symrefTarget: String? = handler.pointed.symref_target?.toKString()
}
