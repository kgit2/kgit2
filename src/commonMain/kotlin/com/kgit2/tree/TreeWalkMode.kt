package com.kgit2.tree

import libgit2.GIT_TREEWALK_POST
import libgit2.GIT_TREEWALK_PRE
import libgit2.git_treewalk_mode

enum class TreeWalkMode(val value: git_treewalk_mode) {
    PreOrder(GIT_TREEWALK_PRE),
    PostOrder(GIT_TREEWALK_POST),
    ;

    companion object {
        fun fromRaw(value: git_treewalk_mode): TreeWalkMode {
            return when (value) {
                GIT_TREEWALK_PRE -> PreOrder
                GIT_TREEWALK_POST -> PostOrder
                else -> error("Unknown tree walk mode: $value")
            }
        }
    }
}
