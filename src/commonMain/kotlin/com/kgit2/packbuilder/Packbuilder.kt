package com.kgit2.packbuilder

import cnames.structs.git_packbuilder
import com.kgit2.annotations.Raw
import com.kgit2.checkout.IndexerProgressCallback
import com.kgit2.checkout.IndexerProgressCallbackPayload
import com.kgit2.checkout.staticIndexerProgressCallback
import com.kgit2.common.extend.asStableRef
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.option.mutually.FileMode
import com.kgit2.memory.CallbackAble
import com.kgit2.memory.ICallbacksPayload
import com.kgit2.memory.RawWrapper
import com.kgit2.model.Buf
import com.kgit2.oid.Oid
import com.kgit2.repository.Repository
import com.kgit2.rev.Revwalk
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import libgit2.git_packbuilder_foreach
import libgit2.git_packbuilder_hash
import libgit2.git_packbuilder_insert
import libgit2.git_packbuilder_insert_commit
import libgit2.git_packbuilder_insert_recur
import libgit2.git_packbuilder_insert_tree
import libgit2.git_packbuilder_insert_walk
import libgit2.git_packbuilder_name
import libgit2.git_packbuilder_new
import libgit2.git_packbuilder_object_count
import libgit2.git_packbuilder_set_callbacks
import libgit2.git_packbuilder_set_threads
import libgit2.git_packbuilder_write
import libgit2.git_packbuilder_write_buf
import libgit2.git_packbuilder_written

@Raw(
    base = git_packbuilder::class,
    free = "git_packbuilder_free",
)
class Packbuilder(
    raw: PackbuilderRaw = PackbuilderRaw(),
    initial: Packbuilder.() -> Unit = {},
) : RawWrapper<git_packbuilder, PackbuilderRaw>(raw),
    CallbackAble<git_packbuilder, PackbuilderRaw, Packbuilder.CallbacksPayload> {
    constructor(secondaryInitial: PackbuilderSecondaryInitial) : this(PackbuilderRaw(secondaryInitial = secondaryInitial))

    constructor(repository: Repository) : this(secondaryInitial = {
        git_packbuilder_new(this.ptr, repository.raw.handler).errorCheck()
    })

    override val callbacksPayload: CallbacksPayload = CallbacksPayload()

    override val stableRef: StableRef<CallbacksPayload> = callbacksPayload.asStableRef()

    var progressCallback: PackbuilderProgressCallback? by callbacksPayload::packbuilderProgressCallback

    val name: String = git_packbuilder_name(raw.handler)!!.toKString()

    val objectCount: ULong
        get() = git_packbuilder_object_count(raw.handler)

    init {
        this.initial()
    }

    fun write(path: String, fileMode: FileMode, progressCallback: IndexerProgressCallback) {
        val callbacksPayload = object : IndexerProgressCallbackPayload {
            override var indexerProgressCallback: IndexerProgressCallback? = progressCallback
        }.asStableRef()
        git_packbuilder_write(
            raw.handler,
            path,
            fileMode.value,
            staticIndexerProgressCallback,
            callbacksPayload.asCPointer()
        ).errorCheck()
        callbacksPayload.dispose()
    }

    fun writeBuffer(buf: Buf) {
        git_packbuilder_write_buf(buf.raw.handler, raw.handler).errorCheck()
    }

    fun written(): ULong {
        return git_packbuilder_written(raw.handler)
    }

    fun forEach(callback: PackbuilderForeachCallback) {
        val callbacksPayload = object : PackbuilderForeachCallbackPayload {
            override var packbuilderForeachCallback: PackbuilderForeachCallback? = callback
        }.asStableRef()
        git_packbuilder_foreach(
            raw.handler,
            staticPackbuilderForeachCallback,
            callbacksPayload.asCPointer()
        ).errorCheck()
        callbacksPayload.dispose()
    }

    fun hash(): Oid? = git_packbuilder_hash(raw.handler)?.let { Oid(handler = it) }

    fun insert(id: Oid, name: String? = null, recursive: Boolean = false) {
        when (recursive) {
            true -> git_packbuilder_insert_recur(raw.handler, id.raw.handler, name).errorCheck()
            false -> git_packbuilder_insert(raw.handler, id.raw.handler, name).errorCheck()
        }
    }

    fun insertCommit(commitId: Oid) {
        git_packbuilder_insert_commit(raw.handler, commitId.raw.handler).errorCheck()
    }

    fun insertTree(treeId: Oid) {
        git_packbuilder_insert_tree(raw.handler, treeId.raw.handler).errorCheck()
    }

    fun insertWalk(walk: Revwalk) {
        git_packbuilder_insert_walk(raw.handler, walk.raw.handler).errorCheck()
    }

    fun setThreads(threadCount: UInt): UInt {
        return git_packbuilder_set_threads(raw.handler, threadCount)
    }

    override fun hashCode(): Int {
        return hash().hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Packbuilder) return false
        if (hash() != other.hash()) return false
        return true
    }

    inner class CallbacksPayload : ICallbacksPayload, PackbuilderProgressCallbackPayload {
        override var packbuilderProgressCallback: PackbuilderProgressCallback? = null
            set(value) {
                field = value
                if (value != null) {
                    git_packbuilder_set_callbacks(raw.handler, staticPackbuilderProgressCallback, stableRef.asCPointer())
                } else {
                    git_packbuilder_set_callbacks(raw.handler, null, null)
                }
            }
    }
}
