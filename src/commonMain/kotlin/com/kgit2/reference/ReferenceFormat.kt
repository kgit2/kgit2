package com.kgit2.reference

import com.kgit2.annotations.FlagMask
import com.kgit2.common.option.BaseMultiple
import kotlinx.cinterop.convert
import libgit2.*

@FlagMask(
    flagsType = git_reference_format_t::class,
    "GIT_REFERENCE_FORMAT_NORMAL",
    "GIT_REFERENCE_FORMAT_ALLOW_ONELEVEL",
    "GIT_REFERENCE_FORMAT_REFSPEC_PATTERN",
    "GIT_REFERENCE_FORMAT_REFSPEC_SHORTHAND",
)
data class ReferenceFormat(
    override var flags: git_reference_format_t,
    override val onFlagsChanged: ((UInt) -> Unit)? = null,
) : ReferenceFormatMask<ReferenceFormat>
