package com.kgit2.factory

import com.kgit2.common.GitOpts

object GitOptsFactory {
    fun createGitOptsParameter(option: GitOpts): Any? {
        return when (option) {
            GitOpts.GIT_OPT_GET_MWINDOW_SIZE -> null
            GitOpts.GIT_OPT_SET_MWINDOW_SIZE -> TODO()
            GitOpts.GIT_OPT_GET_MWINDOW_MAPPED_LIMIT -> TODO()
            GitOpts.GIT_OPT_SET_MWINDOW_MAPPED_LIMIT -> TODO()
            GitOpts.GIT_OPT_GET_SEARCH_PATH -> TODO()
            GitOpts.GIT_OPT_SET_SEARCH_PATH -> TODO()
            GitOpts.GIT_OPT_SET_CACHE_OBJECT_LIMIT -> TODO()
            GitOpts.GIT_OPT_SET_CACHE_MAX_SIZE -> TODO()
            GitOpts.GIT_OPT_ENABLE_CACHING -> TODO()
            GitOpts.GIT_OPT_GET_CACHED_MEMORY -> TODO()
            GitOpts.GIT_OPT_GET_TEMPLATE_PATH -> TODO()
            GitOpts.GIT_OPT_SET_TEMPLATE_PATH -> TODO()
            GitOpts.GIT_OPT_SET_SSL_CERT_LOCATIONS -> TODO()
            GitOpts.GIT_OPT_SET_USER_AGENT -> TODO()
            GitOpts.GIT_OPT_ENABLE_STRICT_OBJECT_CREATION -> TODO()
            GitOpts.GIT_OPT_ENABLE_STRICT_SYMBOLIC_REF_CREATION -> TODO()
            GitOpts.GIT_OPT_SET_SSL_CIPHERS -> TODO()
            GitOpts.GIT_OPT_GET_USER_AGENT -> TODO()
            GitOpts.GIT_OPT_ENABLE_OFS_DELTA -> TODO()
            GitOpts.GIT_OPT_ENABLE_FSYNC_GITDIR -> TODO()
            GitOpts.GIT_OPT_GET_WINDOWS_SHAREMODE -> TODO()
            GitOpts.GIT_OPT_SET_WINDOWS_SHAREMODE -> TODO()
            GitOpts.GIT_OPT_ENABLE_STRICT_HASH_VERIFICATION -> TODO()
            GitOpts.GIT_OPT_SET_ALLOCATOR -> TODO()
            GitOpts.GIT_OPT_ENABLE_UNSAVED_INDEX_SAFETY -> TODO()
            GitOpts.GIT_OPT_GET_PACK_MAX_OBJECTS -> TODO()
            GitOpts.GIT_OPT_SET_PACK_MAX_OBJECTS -> TODO()
            GitOpts.GIT_OPT_DISABLE_PACK_KEEP_FILE_CHECKS -> TODO()
            GitOpts.GIT_OPT_ENABLE_HTTP_EXPECT_CONTINUE -> TODO()
            GitOpts.GIT_OPT_GET_MWINDOW_FILE_LIMIT -> TODO()
            GitOpts.GIT_OPT_SET_MWINDOW_FILE_LIMIT -> TODO()
            GitOpts.GIT_OPT_SET_ODB_PACKED_PRIORITY -> TODO()
            GitOpts.GIT_OPT_SET_ODB_LOOSE_PRIORITY -> TODO()
            GitOpts.GIT_OPT_GET_EXTENSIONS -> TODO()
            GitOpts.GIT_OPT_SET_EXTENSIONS -> TODO()
            GitOpts.GIT_OPT_GET_OWNER_VALIDATION -> TODO()
            GitOpts.GIT_OPT_SET_OWNER_VALIDATION -> TODO()
        }
    }
}
