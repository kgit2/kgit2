package com.kgit2.index

import com.kgit2.annotations.InitialPointerType
import com.kgit2.annotations.Raw

@Raw(
    base = "git_index",
    initialPointer = InitialPointerType.SECONDARY,
    free = "git_index_free",
    shouldFreeOnFailure = true
)
class Index {
}
