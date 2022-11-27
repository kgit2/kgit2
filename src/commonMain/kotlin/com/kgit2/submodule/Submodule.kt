package com.kgit2.submodule

import cnames.structs.git_submodule
import com.kgit2.annotations.Raw
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.extend.toInt
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import com.kgit2.model.Oid
import com.kgit2.repository.Repository
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import libgit2.*

@Raw(
    base = git_submodule::class,
    free = "git_submodule_free",
)
class Submodule(raw: SubmoduleRaw) : GitBase<git_submodule, SubmoduleRaw>(raw) {
    constructor(memory: Memory, handler: SubmodulePointer) : this(SubmoduleRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        secondary: SubmoduleSecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: SubmoduleSecondaryInitial? = null,
    ) : this(SubmoduleRaw(memory, secondary, secondaryInitial))

    val name: String? = git_submodule_name(raw.handler)?.toKString()

    val url: String? = git_submodule_url(raw.handler)?.toKString()

    val branch: String? = git_submodule_branch(raw.handler)?.toKString()

    val path: String? = git_submodule_path(raw.handler)?.toKString()

    val headId: Oid? = git_submodule_head_id(raw.handler)?.let { Oid(Memory(), it) }

    val indexId: Oid? = git_submodule_index_id(raw.handler)?.let { Oid(Memory(), it) }

    val workdirId: Oid? = git_submodule_wd_id(raw.handler)?.let { Oid(Memory(), it) }

    val ignoreRule: SubmoduleIgnore = SubmoduleIgnore.fromRaw(git_submodule_ignore(raw.handler))

    val updateStrategy: SubmoduleUpdate = SubmoduleUpdate.fromRaw(git_submodule_update_strategy(raw.handler))

    fun clone(options: SubmoduleUpdateOptions? = null): Repository = Repository {
        git_submodule_clone(this.ptr, raw.handler, options?.raw?.handler).errorCheck()
    }

    fun initial(overwrite: Boolean = false) {
        git_submodule_init(raw.handler, overwrite.toInt()).errorCheck()
    }

    fun open(): Repository = Repository {
        git_submodule_open(this.ptr, raw.handler).errorCheck()
    }

    fun reload(force: Boolean = false) {
        git_submodule_reload(raw.handler, force.toInt()).errorCheck()
    }

    fun sync() {
        git_submodule_sync(raw.handler).errorCheck()
    }

    fun addToIndex(writeIndex: Boolean = true) {
        git_submodule_add_to_index(raw.handler, writeIndex.toInt()).errorCheck()
    }

    fun addFinalize() {
        git_submodule_add_finalize(raw.handler).errorCheck()
    }

    fun update(init: Boolean = false, options: SubmoduleUpdateOptions? = null) {
        git_submodule_update(raw.handler, init.toInt(), options?.raw?.handler).errorCheck()
    }
}
