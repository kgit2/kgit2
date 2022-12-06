package com.kgit2.common.option.mutually

import libgit2.GIT_FILEMODE_BLOB
import libgit2.GIT_FILEMODE_BLOB_EXECUTABLE
import libgit2.GIT_FILEMODE_COMMIT
import libgit2.GIT_FILEMODE_LINK
import libgit2.GIT_FILEMODE_TREE
import libgit2.GIT_FILEMODE_UNREADABLE
import libgit2.git_filemode_t

enum class FileMode(val value: git_filemode_t) {
    UnReadAble(GIT_FILEMODE_UNREADABLE),
    Tree(GIT_FILEMODE_TREE),
    Blob(GIT_FILEMODE_BLOB),
    Executable(GIT_FILEMODE_BLOB_EXECUTABLE),
    Link(GIT_FILEMODE_LINK),
    Commit(GIT_FILEMODE_COMMIT);

    companion object {
        fun fromRaw(value: git_filemode_t): FileMode {
            return when (value) {
                GIT_FILEMODE_UNREADABLE -> UnReadAble
                GIT_FILEMODE_TREE -> Tree
                GIT_FILEMODE_BLOB -> Blob
                GIT_FILEMODE_BLOB_EXECUTABLE -> Executable
                GIT_FILEMODE_LINK -> Link
                GIT_FILEMODE_COMMIT -> Commit
                else -> error("Unknown file mode: $value")
            }
        }
    }
}
