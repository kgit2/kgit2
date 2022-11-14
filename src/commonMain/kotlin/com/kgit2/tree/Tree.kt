package com.kgit2.tree

import com.kgit2.model.AutoFreeGitBase
import kotlinx.cinterop.Arena
import kotlinx.cinterop.CPointer
import libgit2.git_tree
import libgit2.git_tree_free

class Tree(
    override val handler: CPointer<git_tree>,
    override val arena: Arena,
): AutoFreeGitBase<CPointer<git_tree>> {
    override fun free() {
        git_tree_free(handler)
        super.free()
    }

    // TODO: Implement the rest of the API
}
