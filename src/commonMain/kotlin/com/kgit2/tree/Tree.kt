package com.kgit2.tree

import cnames.structs.git_tree
import com.kgit2.annotations.Raw
import com.kgit2.callback.TreeWalkCallback
import com.kgit2.common.error.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import com.kgit2.model.Oid
import com.kgit2.`object`.Object
import kotlinx.cinterop.*
import libgit2.*

@Raw(
    base = git_tree::class,
    free = "git_tree_free",
)
class Tree(raw: TreeRaw) : GitBase<git_tree, TreeRaw>(raw) {
    constructor(memory: Memory, handler: CPointer<git_tree>) : this(TreeRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        secondary: TreeSecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: TreeSecondaryInitial? = null,
    ) : this(TreeRaw(memory, secondary, secondaryInitial))

    val id: Oid = Oid(Memory(), git_tree_id(raw.handler)!!)

    val length = git_tree_entrycount(raw.handler)

    val isEmpty: Boolean
        get() = length == 0UL

    fun walk(mode: TreeWalkMode, callback: TreeWalkCallback) {
        val gitCallback: git_treewalk_cb = staticCFunction { root, entry, payload ->
            payload!!.asStableRef<TreeWalkCallback>()
                .get()
                .treeWalk(root!!.toKString(), TreeEntry(Memory(), entry!!))
        }
        git_tree_walk(raw.handler, mode.value, gitCallback, StableRef.create(callback).asCPointer()).errorCheck()
    }

    fun getEntryById(id: Oid): TreeEntry {
        val entryHandler = git_tree_entry_byid(raw.handler, id.raw.handler)
        return TreeEntry(Memory(), entryHandler!!)
    }

    operator fun get(index: ULong): TreeEntry = getEntryByIndex(index)

    fun getEntryByIndex(index: ULong): TreeEntry {
        val entryHandler = git_tree_entry_byindex(raw.handler, index)
        return TreeEntry(Memory(), entryHandler!!)
    }

    fun getEntryByName(name: String): TreeEntry {
        val entryHandler = git_tree_entry_byname(raw.handler, name)
        return TreeEntry(Memory(), entryHandler!!)
    }

    fun getEntryByPath(path: String): TreeEntry {
        return TreeEntry {
            git_tree_entry_bypath(this.ptr, raw.handler, path).errorCheck()
        }
    }

    fun asObject(): Object {
        raw.move()
        return Object(memory = raw.memory, raw.handler.reinterpret())
    }
}
