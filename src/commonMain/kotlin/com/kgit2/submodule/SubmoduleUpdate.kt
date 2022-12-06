package com.kgit2.submodule

import libgit2.GIT_SUBMODULE_UPDATE_CHECKOUT
import libgit2.GIT_SUBMODULE_UPDATE_DEFAULT
import libgit2.GIT_SUBMODULE_UPDATE_MERGE
import libgit2.GIT_SUBMODULE_UPDATE_NONE
import libgit2.GIT_SUBMODULE_UPDATE_REBASE
import libgit2.git_submodule_update_t

enum class SubmoduleUpdate(val value: git_submodule_update_t) {
    Checkout(GIT_SUBMODULE_UPDATE_CHECKOUT),
    Rebase(GIT_SUBMODULE_UPDATE_REBASE),
    Merge(GIT_SUBMODULE_UPDATE_MERGE),
    None(GIT_SUBMODULE_UPDATE_NONE),
    Default(GIT_SUBMODULE_UPDATE_DEFAULT),
    ;

    companion object {
        fun fromRaw(value: git_submodule_update_t): SubmoduleUpdate {
            return when (value) {
                GIT_SUBMODULE_UPDATE_CHECKOUT -> Checkout
                GIT_SUBMODULE_UPDATE_REBASE -> Rebase
                GIT_SUBMODULE_UPDATE_MERGE -> Merge
                GIT_SUBMODULE_UPDATE_NONE -> None
                GIT_SUBMODULE_UPDATE_DEFAULT -> Default
                else -> error("Unknown value: $value")
            }
        }
    }
}
