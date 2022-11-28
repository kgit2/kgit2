package com.kgit2.index

import cnames.structs.git_index
import com.kgit2.annotations.Raw
import com.kgit2.callback.IndexMatchedPathCallback
import com.kgit2.common.extend.asCPointer
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.extend.toBoolean
import com.kgit2.common.extend.toInt
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
import com.kgit2.model.Oid
import com.kgit2.model.withGitStrArray
import com.kgit2.repository.Repository
import com.kgit2.tree.Tree
import kotlinx.atomicfu.atomic
import kotlinx.cinterop.*
import libgit2.*

@Raw(
    base = git_index::class,
    free = "git_index_free",
)
class Index(raw: IndexRaw) : GitBase<git_index, IndexRaw>(raw), Iterable<IndexEntry> {
    constructor(memory: Memory, handler: IndexPointer) : this(IndexRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        secondary: IndexSecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: IndexSecondaryInitial = {
            git_index_new(this.ptr)
        }
    ) : this(IndexRaw(memory, secondary, secondaryInitial))

    constructor(path: String) : this(secondaryInitial = {
        git_index_open(this.ptr, path)
    })

    var version = git_index_version(raw.handler)
        set(value) {
            field = value
            git_index_set_version(raw.handler, value)
        }

    val size: Int = git_index_entrycount(raw.handler).convert()

    val isEmpty = size == 0

    fun add(entry: IndexEntry) {
        git_index_add(raw.handler, entry.memCopy().raw.handler).errorCheck()
    }

    fun addFromBuffer(entry: IndexEntry, buffer: ByteArray) {
        git_index_add_frombuffer(raw.handler, entry.memCopy().raw.handler, buffer.refTo(0), buffer.size.convert()).errorCheck()
    }

    fun addPath(path: String) {
        git_index_add_bypath(raw.handler, path).errorCheck()
    }

    fun addAll(pathSpecs: Collection<String>, options: IndexAddOptions, indexMatchedPathCallback: IndexMatchedPathCallback) {
        withGitStrArray(pathSpecs) { strArray ->
            git_index_add_all(
                raw.handler,
                strArray,
                options.flags,
                indexMatchedPathCallback.toRawCB(),
                indexMatchedPathCallback.asCPointer()
            ).errorCheck()
        }
    }

    fun removeAll(pathSpecs: Collection<String>, indexMatchedPathCallback: IndexMatchedPathCallback?) {
        withGitStrArray(pathSpecs) { strArray ->
            git_index_remove_all(
                raw.handler,
                strArray,
                indexMatchedPathCallback?.toRawCB(),
                indexMatchedPathCallback?.asCPointer()
            ).errorCheck()
        }
    }

    fun updateAll(pathSpecs: Collection<String>, indexMatchedPathCallback: IndexMatchedPathCallback?) {
        withGitStrArray(pathSpecs) { strArray ->
            git_index_update_all(
                raw.handler,
                strArray,
                indexMatchedPathCallback?.toRawCB(),
                indexMatchedPathCallback?.asCPointer()
            ).errorCheck()
        }
    }

    fun clear() {
        git_index_clear(raw.handler).errorCheck()
    }

    operator fun get(index: Int): IndexEntry = IndexEntry(handler = git_index_get_byindex(raw.handler, index.convert())!!)

    override fun iterator(): Iterator<IndexEntry> = IndexEntryIterator()

    inner class IndexEntryIterator : Iterator<IndexEntry> {
        private val index = atomic(0)
        override fun hasNext(): Boolean = index.value < size
        override fun next(): IndexEntry = get(index.incrementAndGet())
    }

    fun get(path: String, stage: Int): IndexEntry? = git_index_get_bypath(raw.handler, path, stage.convert())?.let {
        IndexEntry(handler = it)
    }

    fun removeByPath(path: String) = git_index_remove_bypath(raw.handler, path).errorCheck()

    fun removeDir(path: String, stage: Int) = git_index_remove_directory(raw.handler, path, stage.convert()).errorCheck()

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
