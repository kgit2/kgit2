package com.floater.git.config

import cnames.structs.git_config
import com.floater.git.common.errorCheck
import com.floater.git.model.GitBase
import com.floater.git.model.GitBuf
import com.floater.git.model.impl.GitBufImpl
import kotlinx.cinterop.Arena
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.cValue
import kotlinx.cinterop.memScoped
import libgit2.git_buf
import libgit2.git_config_find_global

class Config : GitBase<CPointer<git_config>> {
    override var handler: CPointer<git_config>? = null
    override val arena: Arena = Arena()

    companion object {
        fun findGlobal(): String {
            memScoped {
                val buf: GitBuf = GitBufImpl(this@memScoped)
                // buf.handler = cValue<git_buf>().ptr
                git_config_find_global(buf.handler).errorCheck()
                println(buf.ptr)
                return buf.ptr!!
            }
        }
    }
    // fun path() {
    //     memScoped {
    //         val pointer = cValue<git_buf>().ptr
    //         git_config_find_global(pointer).errorCheck()
    //         println(pointer.pointed.ptr?.toKString())
    //         println(pointer.pointed.size)
    //         println(pointer.pointed.reserved)
    //         git_config_find_programdata(pointer).errorCheck()
    //         println(pointer.pointed.ptr?.toKString())
    //         println(pointer.pointed.size)
    //         println(pointer.pointed.reserved)
    //     }
    // }
}
