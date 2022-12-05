package com.kgit2.utils

import com.kgit2.branch.Branch
import com.kgit2.oid.Oid
import com.kgit2.repository.Repository
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

fun commit(repository: Repository): Pair<Oid, Oid> {
    val index = repository.Index.index()
    val root = repository.path.toPath(true).parent!!
    FileSystem.SYSTEM.openReadWrite(root / "foo", mustCreate = true, mustExist = false)
    index.addPath("foo")
    val treeId = index.writeTree()
    val tree = repository.Tree.findTree(treeId)
    val signature = repository.Signature.signature()
    val headId = repository.Oid.refNameToOid("HEAD")
    val parent = repository.Commit.findCommit(headId)
    val commitId = repository.Commit.commit("HEAD", signature, signature, "Initial Commit", tree, listOf(parent))
    return commitId to treeId
}
