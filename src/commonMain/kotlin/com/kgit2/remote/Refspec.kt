package com.kgit2.remote

import cnames.structs.git_refspec
import com.kgit2.annotations.Raw
import com.kgit2.common.error.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.fetch.Direction
import com.kgit2.memory.GitBase
import com.kgit2.model.toKString
import com.kgit2.model.withGitBuf
import kotlinx.cinterop.toKString
import libgit2.*

@Raw(
    base = "git_refspec",
    free = "git_refspec_free"
)
class Refspec(
    raw: RefspecRaw,
) : GitBase<git_refspec, RefspecRaw>(raw) {
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

    fun transform(refName: String): String = withGitBuf { buf ->
        git_refspec_transform(buf, raw.handler, refName).errorCheck()
        buf.toKString()!!
    }

    fun rtransform(refName: String): String = withGitBuf { buf ->
        git_refspec_rtransform(buf, raw.handler, refName).errorCheck()
        buf.toKString()!!
    }
}
