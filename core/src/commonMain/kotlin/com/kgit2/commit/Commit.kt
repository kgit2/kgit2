package com.kgit2.commit

import cnames.structs.git_commit
import cnames.structs.git_tree
import com.kgit2.common.error.errorCheck
import com.kgit2.model.AutoFreeGitBase
import com.kgit2.model.Oid
import com.kgit2.`object`.Object
import com.kgit2.signature.MailMap
import com.kgit2.signature.Signature
import com.kgit2.time.Time
import com.kgit2.tree.Tree
import kotlinx.cinterop.*
import libgit2.*

class Commit(
    override val handler: CPointer<git_commit>,
    override val arena: Arena,
) : AutoFreeGitBase<CPointer<git_commit>> {
    override fun free() {
        git_commit_free(handler)
        super.free()
    }

    val id: Oid = Oid(git_commit_id(handler)!!, arena)

    val treeId: Oid = Oid(git_commit_tree_id(handler)!!, arena)

    val tree: Tree = run {
        val arena = Arena()
        val handler = arena.allocPointerTo<git_tree>()
        git_commit_tree(handler.ptr, this.handler).errorCheck()
        Tree(handler.value!!, arena)
    }

    val messageEncoding: String = git_commit_message_encoding(handler)!!.toKString()

    val message: String = git_commit_message(handler)!!.toKString()

    val rawMessage: String = git_commit_message_raw(handler)!!.toKString()

    val rawHeader: String = git_commit_raw_header(handler)!!.toKString()

    val summary: String = git_commit_summary(handler)!!.toKString()

    val body: String = git_commit_body(handler)!!.toKString()

    val time: Time = Time.new(git_commit_time(handler), git_commit_time_offset(handler))

    val parentCount: UInt = git_commit_parentcount(handler)

    val parents: List<Commit> = run {
        (0 until parentCount.convert()).fold(mutableListOf()) { prev, i ->
            val arena = Arena()
            val commit = arena.allocPointerTo<git_commit>()
            git_commit_parent(commit.ptr, handler, i.toUInt()).errorCheck()
            prev.add(Commit(commit.value!!, arena))
            prev
        }
    }

    val author: Signature = Signature.fromHandler(git_commit_author(handler)!!, arena)

    fun authorWithMailMap(mailMap: MailMap): Signature {
        val arena = Arena()
        val signature = arena.allocPointerTo<git_signature>()
        git_commit_author_with_mailmap(signature.ptr, handler, mailMap.handler).errorCheck()
        return Signature.fromHandler(signature.value!!, arena)
    }

    val committer: Signature = Signature.fromHandler(git_commit_committer(handler)!!, arena)

    fun committerWithMailMap(mailMap: MailMap): Signature {
        val arena = Arena()
        val signature = arena.allocPointerTo<git_signature>()
        git_commit_committer_with_mailmap(signature.ptr, handler, mailMap.handler).errorCheck()
        return Signature.fromHandler(signature.value!!, arena)
    }

    fun amend(
        updateRef: String?,
        author: Signature,
        committer: Signature,
        messageEncoding: String,
        message: String,
        tree: Tree,
    ): Oid {
        val arena = Arena()
        val oid = arena.alloc<git_oid>()
        git_commit_amend(
            oid.ptr,
            handler,
            updateRef,
            author.handler,
            committer.handler,
            messageEncoding,
            message,
            tree.handler,
        ).errorCheck()
        return Oid(oid.ptr, arena)
    }

    fun asObject(): Object {
        val arena = Arena()
        val `object` = handler.reinterpret<git_object>()
        return Object(`object`, arena)
    }
}
