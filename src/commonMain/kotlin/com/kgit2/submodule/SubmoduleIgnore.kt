package com.kgit2.submodule

import libgit2.*

enum class SubmoduleIgnore(val value: git_submodule_ignore_t) {
    None(GIT_SUBMODULE_IGNORE_NONE),
    Untracked(GIT_SUBMODULE_IGNORE_UNTRACKED),
    Dirty(GIT_SUBMODULE_IGNORE_DIRTY),
    All(GIT_SUBMODULE_IGNORE_ALL),
    Unspecified(GIT_SUBMODULE_IGNORE_UNSPECIFIED),
    ;

    companion object {
        fun from(value: git_submodule_ignore_t): SubmoduleIgnore {
            return when (value) {
                GIT_SUBMODULE_IGNORE_NONE -> None
                GIT_SUBMODULE_IGNORE_UNTRACKED -> Untracked
                GIT_SUBMODULE_IGNORE_DIRTY -> Dirty
                GIT_SUBMODULE_IGNORE_ALL -> All
                GIT_SUBMODULE_IGNORE_UNSPECIFIED -> Unspecified
                else -> error("Unknown value: $value")
            }
        }
    }
}
