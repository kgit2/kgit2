package com.kgit2.common

import com.kgit2.KGit2

fun kgitRunTest(testBody: () -> Unit) {
    KGit2.initial()
    testBody.invoke()
    KGit2.shutdown()
}
