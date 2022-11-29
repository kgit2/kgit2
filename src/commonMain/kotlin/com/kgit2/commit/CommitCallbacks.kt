package com.kgit2.commit

import com.kgit2.common.error.GitErrorCode
import com.kgit2.oid.Oid
import com.kgit2.signature.Signature
import com.kgit2.tree.Tree

typealias CommitCreateCallback = (
    id: Oid,
    author: Signature?,
    committer: Signature?,
    messageEncoding: String?,
    message: String?,
    tree: Tree,
    parents: List<Commit>,
) -> GitErrorCode
