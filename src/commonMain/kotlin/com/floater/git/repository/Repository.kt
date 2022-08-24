package com.floater.git.repository

import com.floater.git.common.errorCheck
import com.floater.git.config.Config
import com.floater.git.model.GitBase
import kotlinx.cinterop.Arena
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.cValue
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.objcPtr
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.value
import libgit2.*

class Repository(
    val path: String
) : GitBase<CPointer<git_repository>> {

    override var handler: CPointer<git_repository>? = null
    override val arena: Arena = Arena()

    fun initRepository(bare: Boolean = false) {
        handler = memScoped {
            val pointer = allocPointerTo<git_repository>()
            git_repository_init(pointer.ptr, path, if (bare) 1U else 0U).errorCheck()
            pointer.value!!
        }
    }

    fun openRepository() {
        handler = memScoped {
            val pointer = allocPointerTo<git_repository>()
            git_repository_open(pointer.ptr, path).errorCheck()
            pointer.value!!
        }
    }

    fun free() {
        git_repository_free(handler)
        arena.clear()
    }

    fun config(): Config {
        val config = Config()
        config.handler = memScoped {
            val pointer = allocPointerTo<git_config>()
            git_repository_config(pointer.ptr, handler).errorCheck()
            pointer.value!!
        }
        return config
    }
}
