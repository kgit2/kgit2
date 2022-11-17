package com.kgit2.reference

import libgit2.GIT_REFERENCE_DIRECT
import libgit2.GIT_REFERENCE_SYMBOLIC
import libgit2.git_reference_t

enum class ReferenceType(val value: git_reference_t) {
    Direct(GIT_REFERENCE_DIRECT),
    Symbolic(GIT_REFERENCE_SYMBOLIC);

    companion object {
        fun fromRaw(value: git_reference_t): ReferenceType = when (value) {
            GIT_REFERENCE_DIRECT -> Direct
            GIT_REFERENCE_SYMBOLIC -> Symbolic
            else -> error("Unknown value: $value")
        }
    }
}
