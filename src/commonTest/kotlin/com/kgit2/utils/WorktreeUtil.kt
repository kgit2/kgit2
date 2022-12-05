package com.kgit2.utils

import com.kgit2.branch.Branch
import com.kgit2.repository.Repository

fun initWorktreesEnv(repository: Repository): Pair<TempDir, Branch> {
    val oid = repository.Checkout.head().target!!
    val commit = repository.Commit.findCommit(oid)
    val branch = repository.Branch.createBranch("wt-branch", commit, true)
    val wtDir = TempDir()
    return wtDir to branch
}
