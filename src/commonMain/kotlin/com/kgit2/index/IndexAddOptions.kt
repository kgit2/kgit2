package com.kgit2.index

import com.kgit2.annotations.FlagMask
import com.kgit2.common.option.BaseMultiple
import kotlinx.cinterop.convert
import libgit2.*

@FlagMask(
    flagsType = git_index_add_option_t::class,
    "GIT_INDEX_ADD_DEFAULT",
    "GIT_INDEX_ADD_FORCE",
    "GIT_INDEX_ADD_DISABLE_PATHSPEC_MATCH",
    "GIT_INDEX_ADD_CHECK_PATHSPEC",
)
data class IndexAddOptions(
    override var flags: git_index_add_option_t,
    override val onFlagsChanged: ((UInt) -> Unit)? = null,
) : IndexAddOptionsMask<IndexAddOptions>
