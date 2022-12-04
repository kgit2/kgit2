package com.kgit2.attr

import com.kgit2.annotations.FlagMask

@FlagMask(
    flagsType = Int::class,
    /**
     * Check attribute flags: Reading values from index and working directory.
     *
     * When checking attributes, it is possible to check attribute files
     * in both the working directory (if there is one) and the index (if
     * there is one).  You can explicitly choose where to check and in
     * which order using the following flags.
     *
     * Core git usually checks the working directory then the index,
     * except during a checkout when it checks the index first.  It will
     * use index only for creating archives or for a bare repo (if an
     * index has been specified for the bare repo).
     */
    "GIT_ATTR_CHECK_FILE_THEN_INDEX",
    "GIT_ATTR_CHECK_INDEX_THEN_FILE",
    "GIT_ATTR_CHECK_INDEX_ONLY",

    /**
     * Check attribute flags: controlling extended attribute behavior.
     *
     * Normally, attribute checks include looking in the /etc (or system
     * equivalent) directory for a `gitattributes` file.  Passing this
     * flag will cause attribute checks to ignore that file.
     * equivalent) directory for a `gitattributes` file.  Passing the
     * `GIT_ATTR_CHECK_NO_SYSTEM` flag will cause attribute checks to
     * ignore that file.
     *
     * Passing the `GIT_ATTR_CHECK_INCLUDE_HEAD` flag will use attributes
     * from a `.gitattributes` file in the repository at the HEAD revision.
     *
     * Passing the `GIT_ATTR_CHECK_INCLUDE_COMMIT` flag will use attributes
     * from a `.gitattributes` file in a specific commit.
     */
    "GIT_ATTR_CHECK_NO_SYSTEM",
    "GIT_ATTR_CHECK_INCLUDE_HEAD",
    "GIT_ATTR_CHECK_INCLUDE_COMMIT",
)
data class AttrCheckFlags(
    var value: UInt = 0U,
) : AttrCheckFlagsMask<AttrCheckFlags> {
    override val onFlagsChanged: ((Int) -> Unit)? = null
    override var flags: Int
        get() = value.toInt()
        set(value) {
            this.value = value.toUInt()
        }
}
