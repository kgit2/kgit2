package com.kgit2.repository

import com.kgit2.annotations.FlagMask
import libgit2.*

@FlagMask(
    flagsType = git_repository_open_flag_t::class,
    /**
     * Only open the repository if it can be immediately found in the start_path. Do not walk up from the start_path looking at parent directories.
     */
    "GIT_REPOSITORY_OPEN_NO_SEARCH",

    /**
     * Unless this flag is set, open will not continue searching across filesystem boundaries (i.e. when st_dev changes from the stat system call). For example, searching in a user's home directory at "/home/user/source/" will not return "/.git/" as the found repo if "/" is a different filesystem than "/home".
     */
    "GIT_REPOSITORY_OPEN_CROSS_FS",

    /**
     * Open repository as a bare repo regardless of core.bare config, and defer loading config file for faster setup. Unlike git_repository_open_bare, this can follow gitlinks.
     */
    "GIT_REPOSITORY_OPEN_BARE",

    /**
     * Do not check for a repository by appending /.git to the start_path; only open the repository if start_path itself points to the git directory.
     */
    "GIT_REPOSITORY_OPEN_NO_DOTGIT",

    /**
     * Find and open a git repository, respecting the environment variables used by the git command-line tools. If set, git_repository_open_ext will ignore the other flags and the ceiling_dirs argument, and will allow a NULL path to use GIT_DIR or search from the current directory. The search for a repository will respect $GIT_CEILING_DIRECTORIES and $GIT_DISCOVERY_ACROSS_FILESYSTEM. The opened repository will respect $GIT_INDEX_FILE, $GIT_NAMESPACE, $GIT_OBJECT_DIRECTORY, and $GIT_ALTERNATE_OBJECT_DIRECTORIES. In the future, this flag will also cause git_repository_open_ext to respect $GIT_WORK_TREE and $GIT_COMMON_DIR; currently, git_repository_open_ext with this flag will error out if either $GIT_WORK_TREE or $GIT_COMMON_DIR is set.
     */
    "GIT_REPOSITORY_OPEN_FROM_ENV",
)
data class RepositoryOpenFlags(
    override var flags: UInt = GIT_REPOSITORY_OPEN_FROM_ENV,
) : RepositoryOpenFlagsMask<RepositoryOpenFlags> {
    override val onFlagsChanged: ((UInt) -> Unit)? = null
}

@FlagMask(
    flagsType = git_repository_init_flag_t::class,
    "GIT_REPOSITORY_INIT_BARE",
    "GIT_REPOSITORY_INIT_NO_REINIT",
    "GIT_REPOSITORY_INIT_NO_DOTGIT_DIR",
    "GIT_REPOSITORY_INIT_MKDIR",
    "GIT_REPOSITORY_INIT_MKPATH",
    "GIT_REPOSITORY_INIT_EXTERNAL_TEMPLATE",
    "GIT_REPOSITORY_INIT_RELATIVE_GITLINK",
)
data class RepositoryInitFlags(
    override var flags: UInt,
    override val onFlagsChanged: ((UInt) -> Unit)?,
) : RepositoryInitFlagsMask<RepositoryInitFlags>

enum class RepositoryInitMode(val value: git_repository_init_mode_t) {
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
    ;

    companion object {
        fun from(value: git_repository_init_mode_t): RepositoryInitMode {
            return when (value) {
                GIT_REPOSITORY_INIT_SHARED_UMASK -> Umask
                GIT_REPOSITORY_INIT_SHARED_GROUP -> Group
                GIT_REPOSITORY_INIT_SHARED_ALL -> All
                else -> error("Unknown value: $value")
            }
        }
    }
}
