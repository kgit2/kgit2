package com.kgit2.apply

import com.kgit2.annotations.FlagMask
import libgit2.git_apply_flags_t

@FlagMask(
    flagsType = git_apply_flags_t::class,
    "GIT_APPLY_CHECK"
)
data class ApplyFlags(
    override var flags: git_apply_flags_t,
) : ApplyFlagsMask<ApplyFlags> {
    override val onFlagsChanged: ((UInt) -> Unit)?
        get() = TODO("Not yet implemented")
}
