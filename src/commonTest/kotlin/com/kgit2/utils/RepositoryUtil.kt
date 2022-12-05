package com.kgit2.utils

import com.kgit2.repository.Repository
import com.kgit2.repository.RepositoryInitOptions
import okio.Path

fun initRepository(repoPath: Path): Pair<Path, Repository> {
    val options = RepositoryInitOptions() {
        initialHead = "main"
    }
    val repository = Repository.initialExt(repoPath.toString(), options)
    val config = repository.Config.config()
    config.setString("user.name", "kgit2")
    config.setString("user.email", "kgit2@kgit2.com")
    val index = repository.Index.index()
    val id = index.writeTree()
    val tree = repository.Tree.findTree(id)
    val signature = repository.Signature.signature()
    repository.Commit.commit("HEAD", signature, signature, "Initial Commit", tree)
    return repoPath to repository
}
