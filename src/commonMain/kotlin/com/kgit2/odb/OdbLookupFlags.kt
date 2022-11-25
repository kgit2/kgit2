package com.kgit2.odb

import libgit2.GIT_ODB_LOOKUP_NO_REFRESH
import libgit2.git_odb_lookup_flags_t

enum class OdbLookupFlags(val value: git_odb_lookup_flags_t) {
    NoRefresh(GIT_ODB_LOOKUP_NO_REFRESH),
    ;

    companion object {
        fun fromRaw(raw: git_odb_lookup_flags_t): OdbLookupFlags {
            return when (raw) {
                GIT_ODB_LOOKUP_NO_REFRESH -> NoRefresh
                else -> throw IllegalArgumentException("Unknown OdbLookupFlags: $raw")
            }
        }
    }
}
