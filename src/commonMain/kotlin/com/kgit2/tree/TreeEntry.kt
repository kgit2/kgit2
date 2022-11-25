package com.kgit2.tree

import com.kgit2.common.error.toBoolean
import com.kgit2.common.memory.Memory
import com.kgit2.common.option.mutually.FileMode
import com.kgit2.memory.Raw
import com.kgit2.memory.GitBase
import com.kgit2.model.Oid
import com.kgit2.`object`.Object
import com.kgit2.`object`.ObjectType
import com.kgit2.repository.Repository
import kotlinx.cinterop.*
import libgit2.*

typealias TreeEntryPointer = CPointer<git_tree_entry>

typealias TreeEntrySecondaryPointer = CPointerVar<git_tree_entry>

typealias TreeEntryInitial = TreeEntrySecondaryPointer.(Memory) -> Unit

class TreeEntryRaw(
    memory: Memory,
    handler: CPointer<git_tree_entry>,
) : Raw<git_tree_entry>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: TreeEntrySecondaryPointer = memory.allocPointerTo(),
        initial: TreeEntryInitial? = null,
    ) : this(memory, handler.apply {
        runCatching {
            initial?.invoke(handler, memory)
        }.onFailure {
            git_tree_entry_free(handler.value!!)
            memory.free()
        }.getOrThrow()
    }.value!!)

    override val beforeFree: () -> Unit = {
        git_tree_entry_free(handler)
    }
}

class TreeEntry(raw: TreeEntryRaw) : GitBase<git_tree_entry, TreeEntryRaw>(raw) {
    constructor(memory: Memory, handler: CPointer<git_tree_entry>) : this(TreeEntryRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: TreeEntrySecondaryPointer = memory.allocPointerTo(),
        initial: TreeEntryInitial? = null,
    ) : this(TreeEntryRaw(memory, handler, initial))

    val id: Oid = Oid(Memory(), git_tree_entry_id(raw.handler)!!)

    val name: String = git_tree_entry_name(raw.handler)!!.toKString()

    val type: ObjectType = ObjectType.fromRaw(git_tree_entry_type(raw.handler))

    val fileMode: FileMode = FileMode.fromRaw(git_tree_entry_filemode(raw.handler))

    val fileModeRaw: FileMode = FileMode.fromRaw(git_tree_entry_filemode_raw(raw.handler))

    fun toObject(repository: Repository) = Object() {
        git_tree_entry_to_object(this.ptr, repository.raw.handler, raw.handler)
    }

    fun clone() = TreeEntry() {
        git_tree_entry_dup(this.ptr, raw.handler)
    }

    operator fun compareTo(other: TreeEntry) = git_tree_entry_cmp(raw.handler, other.raw.handler)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TreeEntry) return false
        return compareTo(other).toBoolean()
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + fileMode.hashCode()
        result = 31 * result + fileModeRaw.hashCode()
        return result
    }
}
