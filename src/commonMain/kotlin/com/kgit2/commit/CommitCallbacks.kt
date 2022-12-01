package com.kgit2.commit

import cnames.structs.git_commit
import cnames.structs.git_tree
import com.kgit2.common.error.GitErrorCode
import com.kgit2.oid.Oid
import com.kgit2.signature.Signature
import com.kgit2.tree.Tree
import kotlinx.cinterop.*
import libgit2.git_commit_create_cb
import libgit2.git_oid
import libgit2.git_signature

typealias CommitCreateCallback = (
    id: Oid,
    author: Signature?,
    committer: Signature?,
    messageEncoding: String?,
    message: String?,
    tree: Tree,
    parents: List<Commit>,
) -> GitErrorCode

interface CommitCreateCallbackPayload {
    var commitCreateCallback: CommitCreateCallback?
}

val staticCommitCreateCallback: git_commit_create_cb = staticCFunction {
        id: CPointer<git_oid>?,
        author: CPointer<git_signature>?,
        committer: CPointer<git_signature>?,
        messageEncoding: CPointer<ByteVar>?,
        message: CPointer<ByteVar>?,
        tree: CPointer<git_tree>?,
        parent_count: ULong,
        parents: CArrayPointer<CPointerVar<git_commit>>?,
        payload: COpaquePointer?
    ->
    val callback = payload!!.asStableRef<CommitCreateCallbackPayload>().get()
    callback.commitCreateCallback!!.invoke(
        Oid(handler = id!!),
        author?.let { Signature(handler = it) },
        committer?.let { Signature(handler = it) },
        messageEncoding?.toKString(),
        message?.toKString(),
        Tree(handler = tree!!),
        List(parent_count.convert()) { Commit(handler = parents!![it]!!) },
    ).value
}
