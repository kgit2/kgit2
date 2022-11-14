package com.kgit2.submodule

import cnames.structs.git_repository
import cnames.structs.git_submodule
import com.kgit2.common.error.errorCheck
import com.kgit2.common.error.toInt
import com.kgit2.model.AutoFreeGitBase
import com.kgit2.model.Oid
import com.kgit2.repository.Repository
import kotlinx.cinterop.*
import libgit2.*

class Submodule(
    override val handler: CPointer<git_submodule>,
    override val arena: Arena,
) : AutoFreeGitBase<CPointer<git_submodule>> {
    val name: String? = git_submodule_name(handler)?.toKString()

    val url: String? = git_submodule_url(handler)?.toKString()

    val branch: String? = git_submodule_branch(handler)?.toKString()

    val path: String? = git_submodule_path(handler)?.toKString()

    val headId: Oid? = git_submodule_head_id(handler)?.let { Oid(it, arena) }

    val indexId: Oid? = git_submodule_index_id(handler)?.let { Oid(it, arena) }

    val workdirId: Oid? = git_submodule_wd_id(handler)?.let { Oid(it, arena) }

    val ignoreRule: SubmoduleIgnore = SubmoduleIgnore.fromRaw(git_submodule_ignore(handler))

    val updateStrategy: SubmoduleUpdate = SubmoduleUpdate.fromRaw(git_submodule_update_strategy(handler))

    fun clone(options: SubmoduleUpdateOptions? = null): Repository {
        val arena = Arena()
        val repository = arena.allocPointerTo<git_repository>()
        git_submodule_clone(repository.ptr, handler, options?.handler).errorCheck()
        return Repository(repository.value!!, arena)
    }

    fun initial(overwrite: Boolean = false) {
        git_submodule_init(handler, overwrite.toInt()).errorCheck()
    }

    fun open(): Repository {
        val arena = Arena()
        val repository = arena.allocPointerTo<git_repository>()
        git_submodule_open(repository.ptr, handler).errorCheck()
        return Repository(repository.value!!, arena)
    }

    fun reload(force: Boolean = false) {
        git_submodule_reload(handler, force.toInt()).errorCheck()
    }

    fun sync() {
        git_submodule_sync(handler).errorCheck()
    }

    fun addToIndex(writeIndex: Boolean = true) {
        git_submodule_add_to_index(handler, writeIndex.toInt()).errorCheck()
    }

    fun addFinalize() {
        git_submodule_add_finalize(handler).errorCheck()
    }

    fun update(init: Boolean = false, options: SubmoduleUpdateOptions? = null) {
        git_submodule_update(handler, init.toInt(), options?.handler).errorCheck()
    }
}
