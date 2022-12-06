package com.kgit2.tree

import cnames.structs.git_treebuilder
import com.kgit2.annotations.Raw
import com.kgit2.common.extend.asStableRef
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.option.mutually.FileMode
import com.kgit2.memory.RawWrapper
import com.kgit2.oid.Oid
import kotlinx.cinterop.ptr
import libgit2.git_treebuilder_clear
import libgit2.git_treebuilder_entrycount
import libgit2.git_treebuilder_filter
import libgit2.git_treebuilder_get
import libgit2.git_treebuilder_insert
import libgit2.git_treebuilder_remove
import libgit2.git_treebuilder_write

@Raw(
    base = git_treebuilder::class,
    free = "git_treebuilder_free"
)
class TreeBuilder(
    raw: TreebuilderRaw,
) : RawWrapper<git_treebuilder, TreebuilderRaw>(raw) {
    constructor(secondaryInitial: TreebuilderSecondaryInitial) : this(TreebuilderRaw(secondaryInitial = secondaryInitial))

    fun insert(fileName: String, id: Oid, fileMode: FileMode): TreeEntry = TreeEntry {
        git_treebuilder_insert(
            this.ptr,
            raw.handler,
            fileName,
            id.raw.handler,
            fileMode.value
        )
    }.clone()

    val size: Long
        get() = git_treebuilder_entrycount(raw.handler).toLong()

    fun get(fileName: String): TreeEntry? = git_treebuilder_get(raw.handler, fileName)?.let { TreeEntry(TreeEntryRaw(raw.memory, it)) }

    fun remove(fileName: String) = git_treebuilder_remove(raw.handler, fileName).errorCheck()

    fun write(): Oid = Oid {
        git_treebuilder_write(this, raw.handler).errorCheck()
    }

    fun clear() {
        git_treebuilder_clear(raw.handler).errorCheck()
    }

    fun filter(callback: TreebuilderFilterCallback) {
        val callbackPayload = object : TreebuilderFilterCallbackPayload {
            override var treebuilderFilterCallback: TreebuilderFilterCallback? = callback
        }.asStableRef()
        git_treebuilder_filter(raw.handler, staticTreebuilderFilterCallback, callbackPayload.asCPointer()).errorCheck()
        callbackPayload.dispose()
    }
}
