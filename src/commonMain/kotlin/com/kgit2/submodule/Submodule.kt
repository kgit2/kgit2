package com.kgit2.submodule

import cnames.structs.git_submodule
import com.kgit2.common.error.errorCheck
import com.kgit2.common.error.toInt
import com.kgit2.common.memory.Memory
import com.kgit2.memory.Raw
import com.kgit2.memory.GitBase
import com.kgit2.model.Oid
import com.kgit2.repository.Repository
import kotlinx.cinterop.*
import libgit2.*

typealias SubmodulePointer = CPointer<git_submodule>

typealias SubmoduleSecondaryPointer = CPointerVar<git_submodule>

typealias SubmoduleInitial = SubmoduleSecondaryPointer.(Memory) -> Unit

class SubmoduleRaw(
    memory: Memory,
    handler: SubmodulePointer,
) : Raw<git_submodule>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: SubmoduleSecondaryPointer = memory.allocPointerTo(),
        initial: SubmoduleInitial? = null,
    ) : this(memory, handler.apply {
        runCatching {
            initial?.invoke(handler, memory)
        }.onFailure {
            git_submodule_free(handler.value!!)
            memory.free()
        }.getOrThrow()
    }.value!!)
}

class Submodule(raw: SubmoduleRaw) : GitBase<git_submodule, SubmoduleRaw>(raw) {
    constructor(memory: Memory, handler: SubmodulePointer) : this(SubmoduleRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: SubmoduleSecondaryPointer = memory.allocPointerTo(),
        initial: SubmoduleInitial? = null,
    ) : this(SubmoduleRaw(memory, handler, initial))

    val name: String? = git_submodule_name(raw.handler)?.toKString()

    val url: String? = git_submodule_url(raw.handler)?.toKString()

    val branch: String? = git_submodule_branch(raw.handler)?.toKString()

    val path: String? = git_submodule_path(raw.handler)?.toKString()

    val headId: Oid? = git_submodule_head_id(raw.handler)?.let { Oid(Memory(), it) }

    val indexId: Oid? = git_submodule_index_id(raw.handler)?.let { Oid(Memory(), it) }

    val workdirId: Oid? = git_submodule_wd_id(raw.handler)?.let { Oid(Memory(), it) }

    val ignoreRule: SubmoduleIgnore = SubmoduleIgnore.fromRaw(git_submodule_ignore(raw.handler))

    val updateStrategy: SubmoduleUpdate = SubmoduleUpdate.fromRaw(git_submodule_update_strategy(raw.handler))

    fun clone(options: SubmoduleUpdateOptions? = null): Repository = Repository() {
        git_submodule_clone(this.ptr, raw.handler, options?.raw?.handler).errorCheck()
    }

    fun initial(overwrite: Boolean = false) {
        git_submodule_init(raw.handler, overwrite.toInt()).errorCheck()
    }

    fun open(): Repository = Repository() {
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
