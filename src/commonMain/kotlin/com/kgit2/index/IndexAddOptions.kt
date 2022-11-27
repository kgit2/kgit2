package com.kgit2.index

import com.kgit2.common.option.BaseMultiple
import libgit2.*

data class IndexAddOptions(val value: git_index_add_option_t) : BaseMultiple<IndexAddOptions>() {
    companion object {
        val Default = IndexAddOptions(GIT_INDEX_ADD_DEFAULT)
        val Force = IndexAddOptions(GIT_INDEX_ADD_FORCE)
        val DisablePathspecMatch = IndexAddOptions(GIT_INDEX_ADD_DISABLE_PATHSPEC_MATCH)
        val CheckPathspec = IndexAddOptions(GIT_INDEX_ADD_CHECK_PATHSPEC)
    }

    override val longValue: ULong = value.toULong()
}
