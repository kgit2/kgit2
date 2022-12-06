package com.kgit2.attr

import com.kgit2.common.memory.memoryScoped
import kotlinx.cinterop.cstr
import kotlinx.cinterop.toKString
import libgit2.GIT_ATTR_VALUE_FALSE
import libgit2.GIT_ATTR_VALUE_STRING
import libgit2.GIT_ATTR_VALUE_TRUE
import libgit2.GIT_ATTR_VALUE_UNSPECIFIED
import libgit2.git_attr__false
import libgit2.git_attr__true
import libgit2.git_attr__unset
import libgit2.git_attr_value
import libgit2.git_attr_value_t

enum class AttrValueType(val value: git_attr_value_t) {
    /**
     * The attribute has been left unspecified
     */
    Unspecified(GIT_ATTR_VALUE_UNSPECIFIED),

    /**
     * The attribute has been set
     */
    True(GIT_ATTR_VALUE_TRUE),

    /**
     * The attribute has been unset
     */
    False(GIT_ATTR_VALUE_FALSE),

    /**
     * This attribute has a value
     */
    String(GIT_ATTR_VALUE_STRING),
    ;

    companion object {
        fun from(value: git_attr_value_t): AttrValueType {
            return when (value) {
                GIT_ATTR_VALUE_UNSPECIFIED -> Unspecified
                GIT_ATTR_VALUE_TRUE -> True
                GIT_ATTR_VALUE_FALSE -> False
                GIT_ATTR_VALUE_STRING -> String
                else -> throw IllegalArgumentException("Unknown value: $value")
            }
        }

        fun detect(value: kotlin.String?): AttrValueType = memoryScoped {
            val ptr = when (value) {
                git_attr__true!!.toKString() -> git_attr__true
                git_attr__false!!.toKString() -> git_attr__false
                git_attr__unset!!.toKString() -> git_attr__unset
                else -> value?.cstr?.ptr
            }
            return from(git_attr_value(ptr))
        }

        const val ATTR_TURE = "[internal]__TRUE__"
        const val ATTR_FALSE = "[internal]__FALSE__"
        const val ATTR_UNSET = "[internal]__UNSET__"
    }
}
