package com.kgit2.index

import cnames.structs.git_index
import com.kgit2.annotations.Raw
import com.kgit2.common.extend.asStableRef
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.extend.toBoolean
import com.kgit2.common.extend.toInt
import com.kgit2.common.memory.Memory
import com.kgit2.memory.IterableBase
import com.kgit2.memory.RawWrapper
import com.kgit2.model.StrArray
import com.kgit2.model.toStrArray
import com.kgit2.oid.Oid
import com.kgit2.repository.Repository
import com.kgit2.tree.Tree
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.convert
import kotlinx.cinterop.ptr
import kotlinx.cinterop.usePinned
import libgit2.*

@Raw(
    base = git_index::class,
    free = "git_index_free",
)
class Index(raw: IndexRaw) : RawWrapper<git_index, IndexRaw>(raw), IterableBase<IndexEntry> {
    constructor(
        memory: Memory = Memory(),
        secondary: IndexSecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: IndexSecondaryInitial = {
            git_index_new(this.ptr)
        },
    ) : this(IndexRaw(memory, secondary, secondaryInitial))

    constructor(path: String) : this(secondaryInitial = {
        git_index_open(this.ptr, path)
    })

    var version = git_index_version(raw.handler)
        set(value) {
            field = value
            git_index_set_version(raw.handler, value)
        }

    override val size: Long = git_index_entrycount(raw.handler).convert()

    val isEmpty = size == 0L

    fun add(entry: IndexEntry) {
        git_index_add(raw.handler, entry.memCopy().raw.handler).errorCheck()
    }

    fun addFromBuffer(entry: IndexEntry, buffer: ByteArray) {
        buffer.usePinned {
            git_index_add_frombuffer(
                raw.handler,
                entry.memCopy().raw.handler,
                it.addressOf(0),
                buffer.size.convert()
            ).errorCheck()
        }
    }

    fun addPath(path: String) {
        git_index_add_bypath(raw.handler, path).errorCheck()
    }

    fun addAll(
        pathSpecs: Collection<String>,
        options: IndexAddOptions,
        indexMatchedPathCallback: IndexMatchedPathCallback,
    ) {
        val stableRef = object : IndexMatchedPathCallbackPayload {
            override var indexMatchedPathCallback: IndexMatchedPathCallback? = indexMatchedPathCallback
        }.asStableRef()
        git_index_add_all(
            raw.handler,
            pathSpecs.toStrArray().raw.handler,
            options.flags,
            staticIndexMatchedPathCallback,
            stableRef.asCPointer()
        ).errorCheck()
        stableRef.dispose()
    }

    fun removeAll(
        pathSpecs: Collection<String>,
        indexMatchedPathCallback: IndexMatchedPathCallback?,
    ) {
        val stableRef = object : IndexMatchedPathCallbackPayload {
            override var indexMatchedPathCallback: IndexMatchedPathCallback? = indexMatchedPathCallback
        }.asStableRef()
        git_index_remove_all(
            raw.handler,
            pathSpecs.toStrArray().raw.handler,
            staticIndexMatchedPathCallback,
            stableRef.asCPointer()
        ).errorCheck()
        stableRef.dispose()
    }

    fun updateAll(
        pathSpecs: Collection<String>,
        indexMatchedPathCallback: IndexMatchedPathCallback?,
    ) {
        val stableRef = object : IndexMatchedPathCallbackPayload {
            override var indexMatchedPathCallback: IndexMatchedPathCallback? = indexMatchedPathCallback
        }.asStableRef()
        git_index_update_all(
            raw.handler,
            pathSpecs.toStrArray().raw.handler,
            staticIndexMatchedPathCallback,
            stableRef.asCPointer()
        ).errorCheck()
        stableRef.dispose()
    }

    fun clear() {
        git_index_clear(raw.handler).errorCheck()
    }

    override operator fun get(index: Long): IndexEntry =
        IndexEntry(handler = git_index_get_byindex(raw.handler, index.convert())!!)

    fun get(path: String, stage: Int): IndexEntry? = git_index_get_bypath(raw.handler, path, stage.convert())?.let {
        IndexEntry(handler = it)
    }

    fun removeByPath(path: String) = git_index_remove_bypath(raw.handler, path).errorCheck()

    fun removeDir(path: String, stage: Int) =
        git_index_remove_directory(raw.handler, path, stage.convert()).errorCheck()

    fun conflicts(): IndexConflictIterator = IndexConflictIterator() {
        git_index_conflict_iterator_new(this.ptr, raw.handler)
    }

    fun hasConflicts(): Boolean = git_index_has_conflicts(raw.handler).toBoolean()

    fun removeConflict(path: String) {
        git_index_conflict_remove(raw.handler, path).errorCheck()
    }

    fun cleanupConflicts() = git_index_conflict_cleanup(raw.handler).errorCheck()

    fun read(force: Boolean = false) = git_index_read(raw.handler, force.toInt()).errorCheck()

    fun readTree(tree: Tree) = git_index_read_tree(raw.handler, tree.raw.handler).errorCheck()

    fun write() = git_index_write(raw.handler).errorCheck()

    fun writeTree(): Oid = Oid {
        git_index_write_tree(this, raw.handler).errorCheck()
    }

    fun writeTreeTo(repository: Repository) = Oid {
        git_index_write_tree_to(this, raw.handler, repository.raw.handler).errorCheck()
    }
}
