package com.kgit2.repository

import libgit2.*

enum class RepositoryOpenFlag(val value: git_repository_open_flag_t) {
    OpenNoSearch(GIT_REPOSITORY_OPEN_NO_SEARCH),
    OpenCrossFS(GIT_REPOSITORY_OPEN_CROSS_FS),
    OpenBare(GIT_REPOSITORY_OPEN_BARE),
    OpenNoDot(GIT_REPOSITORY_OPEN_NO_DOTGIT),
    OpenFromENV(GIT_REPOSITORY_OPEN_FROM_ENV),
    ;

    companion object {
        fun fromRaw(value: git_repository_open_flag_t): RepositoryOpenFlag {
            return when (value) {
                GIT_REPOSITORY_OPEN_NO_SEARCH -> OpenNoSearch
                GIT_REPOSITORY_OPEN_CROSS_FS -> OpenCrossFS
                GIT_REPOSITORY_OPEN_BARE -> OpenBare
                GIT_REPOSITORY_OPEN_NO_DOTGIT -> OpenNoDot
                GIT_REPOSITORY_OPEN_FROM_ENV -> OpenFromENV
                else -> error("Unknown value: $value")
            }
        }
    }
}

enum class RepositoryInitFlat(val value: git_repository_init_flag_t) {
    Bare(GIT_REPOSITORY_INIT_BARE),
    NoReInit(GIT_REPOSITORY_INIT_NO_REINIT),
    NoDotDir(GIT_REPOSITORY_INIT_NO_DOTGIT_DIR),
    MkDir(GIT_REPOSITORY_INIT_MKDIR),
    MkPath(GIT_REPOSITORY_INIT_MKPATH),
    ExternalTemplate(GIT_REPOSITORY_INIT_EXTERNAL_TEMPLATE),
    RelativeLink(GIT_REPOSITORY_INIT_RELATIVE_GITLINK),
    ;

    companion object {
        fun fromRaw(value: git_repository_init_flag_t): RepositoryInitFlat {
            return when (value) {
                GIT_REPOSITORY_INIT_BARE -> Bare
                GIT_REPOSITORY_INIT_NO_REINIT -> NoReInit
                GIT_REPOSITORY_INIT_NO_DOTGIT_DIR -> NoDotDir
                GIT_REPOSITORY_INIT_MKDIR -> MkDir
                GIT_REPOSITORY_INIT_MKPATH -> MkPath
                GIT_REPOSITORY_INIT_EXTERNAL_TEMPLATE -> ExternalTemplate
                GIT_REPOSITORY_INIT_RELATIVE_GITLINK -> RelativeLink
                else -> error("Unknown value: $value")
            }
        }
    }
}

enum class RepositoryInitMode(val value: git_repository_init_mode_t) {
    UMask(GIT_REPOSITORY_INIT_SHARED_UMASK),
    Group(GIT_REPOSITORY_INIT_SHARED_GROUP),
    All(GIT_REPOSITORY_INIT_SHARED_ALL),
    ;

    companion object {
        fun fromRaw(value: git_repository_init_mode_t): RepositoryInitMode {
            return when (value) {
                GIT_REPOSITORY_INIT_SHARED_UMASK -> UMask
                GIT_REPOSITORY_INIT_SHARED_GROUP -> Group
                GIT_REPOSITORY_INIT_SHARED_ALL -> All
                else -> error("Unknown value: $value")
            }
        }
    }
}
