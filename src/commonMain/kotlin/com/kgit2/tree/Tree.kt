package com.kgit2.tree

import cnames.structs.git_tree
import com.kgit2.annotations.Raw
import com.kgit2.common.extend.asStableRef
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.RawWrapper
import com.kgit2.`object`.Object
import com.kgit2.oid.Oid
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import libgit2.*

@Raw(
    base = git_tree::class,
    free = "git_tree_free",
)
class Tree(raw: TreeRaw) : RawWrapper<git_tree, TreeRaw>(raw) {
    constructor(memory: Memory = Memory(), handler: CPointer<git_tree>) : this(TreeRaw(memory, handler))

    constructor(
        secondaryInitial: TreeSecondaryInitial? = null,
    ) : this(TreeRaw(secondaryInitial = secondaryInitial))

    val id: Oid = Oid(Memory(), git_tree_id(raw.handler)!!)

    val length = git_tree_entrycount(raw.handler)

    val isEmpty: Boolean
        get() = length == 0UL

    fun walk(mode: TreeWalkMode, callback: TreeWalkCallback) {
        val callbackPayload = object : TreeWalkCallbackPayload {
            override var treeWalkCallback: TreeWalkCallback? = callback
        }.asStableRef()
        git_tree_walk(
            raw.handler,
            mode.value,
            staticTreeWalkCallback,
            callbackPayload.asCPointer()
        ).errorCheck()
        callbackPayload.dispose()
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
