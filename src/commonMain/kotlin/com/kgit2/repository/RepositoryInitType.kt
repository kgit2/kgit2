package com.kgit2.repository

import libgit2.*

enum class RepositoryInitType(val value: git_repository_init_mode_t) {
    /**
     * Use permissions configured by umask - the default.
     */
    Umask(GIT_REPOSITORY_INIT_SHARED_UMASK),

    /**
     * Use "--shared=group" behavior, chmod'ing the new repo to be group writable and "g+sx" for sticky group assignment.
     */
    Group(GIT_REPOSITORY_INIT_SHARED_GROUP),

    /**
     * Use "--shared=all" behavior, adding world readability.
     */
    All(GIT_REPOSITORY_INIT_SHARED_ALL),


}
