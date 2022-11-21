package com.kgit2.tree

import cnames.structs.git_tree
import com.kgit2.common.memory.Memory
import com.kgit2.memory.Binding
import com.kgit2.memory.GitBase
import com.kgit2.model.Oid
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.value
import libgit2.git_tree_entrycount
import libgit2.git_tree_free
import libgit2.git_tree_id

typealias TreePointer = CPointer<git_tree>

typealias TreeSecondaryPointer = CPointerVar<git_tree>

typealias TreeInitial = TreeSecondaryPointer.(Memory) -> Unit

class TreeRaw(
    memory: Memory,
    handler: CPointer<git_tree>,
) : Binding<git_tree>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: TreeSecondaryPointer = memory.allocPointerTo(),
        initial: TreeInitial? = null,
    ) : this(memory, handler.apply {
        runCatching {
            initial?.invoke(handler, memory)
        }.onFailure {
            git_tree_free(handler.value!!)
            memory.free()
        }.getOrThrow()
    }.value!!)

    override val beforeFree: () -> Unit = {
        git_tree_free(handler)
    }
}

class Tree(raw: TreeRaw) : GitBase<git_tree, TreeRaw>(raw) {
    constructor(memory: Memory, handler: CPointer<git_tree>) : this(TreeRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: TreeSecondaryPointer = memory.allocPointerTo(),
        initial: TreeInitial? = null,
    ) : this(TreeRaw(memory, handler, initial))

    val id: Oid = Oid(Memory(), git_tree_id(raw.handler)!!)

    val length = git_tree_entrycount(raw.handler)

    val isEmpty: Boolean
        get() = length == 0UL

    //TODO(): implement TreeEntry
}
