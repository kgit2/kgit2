package com.kgit2.commit

import cnames.structs.git_commit
import com.kgit2.common.error.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.Raw
import com.kgit2.memory.GitBase
import com.kgit2.model.Oid
import com.kgit2.`object`.Object
import com.kgit2.signature.MailMap
import com.kgit2.signature.Signature
import com.kgit2.time.Time
import com.kgit2.tree.Tree
import kotlinx.cinterop.*
import libgit2.*

typealias CommitPointer = CPointer<git_commit>

typealias CommitSecondaryPointer = CPointerVar<git_commit>

typealias CommitInitial = CommitSecondaryPointer.(Memory) -> Unit

class CommitRaw(
    memory: Memory,
    handler: CommitPointer,
) : Raw<git_commit>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: CommitSecondaryPointer = memory.allocPointerTo<git_commit>(),
        initial: CommitInitial? = null,
    ) : this(memory, handler.apply {
        runCatching {
            initial?.invoke(handler, memory)
        }.onFailure {
            git_commit_free(handler.value!!)
            memory.free()
        }.getOrThrow()
    }.value!!)

    override val beforeFree: () -> Unit = {
        git_commit_free(handler)
    }
}

class Commit(
    raw: CommitRaw,
) : GitBase<git_commit, CommitRaw>(raw) {
    constructor(memory: Memory, handler: CommitPointer) : this(CommitRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: CommitSecondaryPointer = memory.allocPointerTo<git_commit>(),
        initial: CommitInitial? = null
    ) : this(CommitRaw(memory, handler, initial))

    val id: Oid = Oid(Memory(), git_commit_id(raw.handler)!!)

    val treeId: Oid = Oid(Memory(), git_commit_tree_id(raw.handler)!!)

    val tree: Tree = Tree() {
        git_commit_tree(this.ptr, raw.handler).errorCheck()
    }

    val messageEncoding: String = git_commit_message_encoding(raw.handler)!!.toKString()

    val message: String = git_commit_message(raw.handler)!!.toKString()

    val rawMessage: String = git_commit_message_raw(raw.handler)!!.toKString()

    val rawHeader: String = git_commit_raw_header(raw.handler)!!.toKString()

    val summary: String = git_commit_summary(raw.handler)!!.toKString()

    val body: String = git_commit_body(raw.handler)!!.toKString()

    val time: Time = Time(raw.memory, git_commit_time(raw.handler), git_commit_time_offset(raw.handler))

    val parentCount: UInt = git_commit_parentcount(raw.handler)

    val parents: List<Commit> = run {
        (0 until parentCount.convert()).fold(mutableListOf()) { prev, i ->
            val commit = CommitRaw() { git_commit_parent(this.ptr, raw.handler, i.convert()).errorCheck() }
            prev.add(Commit(commit))
            prev
        }
    }

    val author: Signature = Signature(Memory(), git_commit_author(raw.handler)!!)

    fun authorWithMailMap(mailMap: MailMap): Signature = Signature() {
        git_commit_author_with_mailmap(this.ptr, raw.handler, mailMap.raw.handler).errorCheck()
    }

    val committer: Signature = Signature(Memory(), git_commit_committer(raw.handler)!!)

    fun committerWithMailMap(mailMap: MailMap) = Signature() {
        git_commit_committer_with_mailmap(ptr, raw.handler, mailMap.raw.handler).errorCheck()
    }

    fun amend(
        updateRef: String?,
        author: Signature,
        committer: Signature,
        messageEncoding: String,
        message: String,
        tree: Tree,
    ): Oid = Oid(initial = { memory ->
        runCatching {
            git_commit_amend(
                this,
                raw.handler,
                updateRef,
                author.raw.handler,
                committer.raw.handler,
                messageEncoding,
                message,
                tree.raw.handler,
            ).errorCheck()
        }.onFailure {
            memory.free()
        }.getOrThrow()
    })

    fun asObject(): Object {
        raw.move()
        return Object(raw.memory, raw.handler.reinterpret())
    }
}
