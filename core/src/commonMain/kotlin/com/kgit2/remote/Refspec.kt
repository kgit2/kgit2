package com.kgit2.remote

import cnames.structs.git_refspec
import com.kgit2.common.error.errorCheck
import com.kgit2.fetch.Direction
import com.kgit2.model.AutoFreeGitBase
import com.kgit2.model.toKString
import com.kgit2.model.withGitBuf
import kotlinx.cinterop.Arena
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.toKString
import libgit2.*

class Refspec(
    override val handler: CPointer<git_refspec>,
    override val arena: Arena,
) : AutoFreeGitBase<CPointer<git_refspec>> {
    override fun free() {
        git_refspec_free(handler)
        super.free()
    }

    val direction = Direction.fromRaw(git_refspec_direction(handler))

    val src = git_refspec_src(handler)?.toKString()

    val dst = git_refspec_dst(handler)?.toKString()

    fun srcMatches(refName: String): Boolean {
        return git_refspec_src_matches(handler, refName) == 1
    }

    fun dstMatches(refName: String): Boolean {
        return git_refspec_dst_matches(handler, refName) == 1
    }

    override fun toString(): String {
        return git_refspec_string(handler)!!.toKString()
    }

    fun transform(refName: String): String = withGitBuf { buf ->
        git_refspec_transform(buf, handler, refName).errorCheck()
        buf.toKString()!!
    }

    fun rtransform(refName: String): String = withGitBuf { buf ->
        git_refspec_rtransform(buf, handler, refName).errorCheck()
        buf.toKString()!!
    }
}
