package com.kgit2.`object`

import libgit2.git_object_string2type
import libgit2.git_object_t

enum class ObjectType(val value: git_object_t) {
    Any(libgit2.GIT_OBJECT_ANY),

    /**< Object can be any of the following */
    Invalid(libgit2.GIT_OBJECT_INVALID),

    /**< Object is invalid. */
    Commit(libgit2.GIT_OBJECT_COMMIT),

    /**< A commit object. */
    Tree(libgit2.GIT_OBJECT_TREE),

    /**< A tree (directory listing) object. */
    Blob(libgit2.GIT_OBJECT_BLOB),

    /**< A file revision object. */
    Tag(libgit2.GIT_OBJECT_TAG),

    /**< An annotated tag object. */
    OFSDelta(libgit2.GIT_OBJECT_OFS_DELTA),

    /**< A delta, base is given by an offset. */
    REFDelta(libgit2.GIT_OBJECT_REF_DELTA);

    /**< A delta, base is given by object id. */

    companion object {
        fun fromString(str: String): ObjectType {
            return when (val result = git_object_string2type(str)) {
                Any.value -> Any
                Invalid.value -> Invalid
                Commit.value -> Commit
                Tree.value -> Tree
                Blob.value -> Blob
                Tag.value -> Tag
                OFSDelta.value -> OFSDelta
                REFDelta.value -> REFDelta
                else -> error("Unknown type: $result")
            }
        }

        fun fromRaw(raw: git_object_t): ObjectType {
            return when (raw) {
                Any.value -> Any
                Invalid.value -> Invalid
                Commit.value -> Commit
                Tree.value -> Tree
                Blob.value -> Blob
                Tag.value -> Tag
                OFSDelta.value -> OFSDelta
                REFDelta.value -> REFDelta
                else -> error("Unknown type: $raw")
            }
        }
    }
}
