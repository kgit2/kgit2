package com.kgit2.remote

import cnames.structs.git_refspec
import com.kgit2.annotations.Raw
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.fetch.Direction
import com.kgit2.memory.RawWrapper
import com.kgit2.model.Buf
import kotlinx.cinterop.toKString
import libgit2.*

@Raw(
    base = git_refspec::class,
    free = "git_refspec_free"
)
class Refspec(raw: RefspecRaw) : RawWrapper<git_refspec, RefspecRaw>(raw) {
    constructor(memory: Memory, handler: RefspecPointer) : this(RefspecRaw(memory, handler))

    val direction = Direction.fromRaw(git_refspec_direction(raw.handler))

    val src = git_refspec_src(raw.handler)?.toKString()

    val dst = git_refspec_dst(raw.handler)?.toKString()

    fun srcMatches(refName: String): Boolean {
        return git_refspec_src_matches(raw.handler, refName) == 1
    }

    fun dstMatches(refName: String): Boolean {
        return git_refspec_dst_matches(raw.handler, refName) == 1
    }

    override fun toString(): String {
        return git_refspec_string(raw.handler)!!.toKString()
    }

    fun transform(refName: String): Buf = Buf {
        git_refspec_transform(this, raw.handler, refName).errorCheck()
    }

    fun rtransform(refName: String): Buf = Buf { buf ->
        git_refspec_rtransform(this, raw.handler, refName).errorCheck()
    }
}
