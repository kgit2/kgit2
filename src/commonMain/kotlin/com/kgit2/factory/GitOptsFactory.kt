package com.kgit2.factory

import com.kgit2.common.GitOptions

object GitOptsFactory {
    fun createGitOptsParameter(option: GitOptions): Any? {
        return when (option) {
            GitOptions.GIT_OPT_GET_MWINDOW_SIZE -> null
            GitOptions.GIT_OPT_SET_MWINDOW_SIZE -> TODO()
            GitOptions.GIT_OPT_GET_MWINDOW_MAPPED_LIMIT -> TODO()
            GitOptions.GIT_OPT_SET_MWINDOW_MAPPED_LIMIT -> TODO()
            GitOptions.GIT_OPT_GET_SEARCH_PATH -> TODO()
            GitOptions.GIT_OPT_SET_SEARCH_PATH -> TODO()
            GitOptions.GIT_OPT_SET_CACHE_OBJECT_LIMIT -> TODO()
            GitOptions.GIT_OPT_SET_CACHE_MAX_SIZE -> TODO()
            GitOptions.GIT_OPT_ENABLE_CACHING -> TODO()
            GitOptions.GIT_OPT_GET_CACHED_MEMORY -> TODO()
            GitOptions.GIT_OPT_GET_TEMPLATE_PATH -> TODO()
            GitOptions.GIT_OPT_SET_TEMPLATE_PATH -> TODO()
            GitOptions.GIT_OPT_SET_SSL_CERT_LOCATIONS -> TODO()
            GitOptions.GIT_OPT_SET_USER_AGENT -> TODO()
            GitOptions.GIT_OPT_ENABLE_STRICT_OBJECT_CREATION -> TODO()
            GitOptions.GIT_OPT_ENABLE_STRICT_SYMBOLIC_REF_CREATION -> TODO()
            GitOptions.GIT_OPT_SET_SSL_CIPHERS -> TODO()
            GitOptions.GIT_OPT_GET_USER_AGENT -> TODO()
            GitOptions.GIT_OPT_ENABLE_OFS_DELTA -> TODO()
            GitOptions.GIT_OPT_ENABLE_FSYNC_GITDIR -> TODO()
            GitOptions.GIT_OPT_GET_WINDOWS_SHAREMODE -> TODO()
            GitOptions.GIT_OPT_SET_WINDOWS_SHAREMODE -> TODO()
            GitOptions.GIT_OPT_ENABLE_STRICT_HASH_VERIFICATION -> TODO()
            GitOptions.GIT_OPT_SET_ALLOCATOR -> TODO()
            GitOptions.GIT_OPT_ENABLE_UNSAVED_INDEX_SAFETY -> TODO()
            GitOptions.GIT_OPT_GET_PACK_MAX_OBJECTS -> TODO()
            GitOptions.GIT_OPT_SET_PACK_MAX_OBJECTS -> TODO()
            GitOptions.GIT_OPT_DISABLE_PACK_KEEP_FILE_CHECKS -> TODO()
            GitOptions.GIT_OPT_ENABLE_HTTP_EXPECT_CONTINUE -> TODO()
            GitOptions.GIT_OPT_GET_MWINDOW_FILE_LIMIT -> TODO()
            GitOptions.GIT_OPT_SET_MWINDOW_FILE_LIMIT -> TODO()
            GitOptions.GIT_OPT_SET_ODB_PACKED_PRIORITY -> TODO()
            GitOptions.GIT_OPT_SET_ODB_LOOSE_PRIORITY -> TODO()
            GitOptions.GIT_OPT_GET_EXTENSIONS -> TODO()
            GitOptions.GIT_OPT_SET_EXTENSIONS -> TODO()
            GitOptions.GIT_OPT_GET_OWNER_VALIDATION -> TODO()
            GitOptions.GIT_OPT_SET_OWNER_VALIDATION -> TODO()
        }
    }
}
