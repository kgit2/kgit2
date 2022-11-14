package com.kgit2.reference

import com.kgit2.common.option.BaseMultiple
import libgit2.*

data class ReferenceFormat(val value: git_reference_format_t) : BaseMultiple<ReferenceFormat>() {
    companion object {
        val Normal = ReferenceFormat(GIT_REFERENCE_FORMAT_NORMAL)
        val AllowOneLevel = ReferenceFormat(GIT_REFERENCE_FORMAT_ALLOW_ONELEVEL)
        val RefspecPattern = ReferenceFormat(GIT_REFERENCE_FORMAT_REFSPEC_PATTERN)
        val RefspecShortHand = ReferenceFormat(GIT_REFERENCE_FORMAT_REFSPEC_SHORTHAND)
    }

    override val longValue: ULong = value.toULong()
}
