@file:Suppress("SpellCheckingInspection")

package com.kgit2.common

import libgit2.git_libgit2_opt_t

enum class GitOptions(val setter: git_libgit2_opt_t, val getter: git_libgit2_opt_t? = null) {
    WindowSize(git_libgit2_opt_t.GIT_OPT_GET_MWINDOW_SIZE, git_libgit2_opt_t.GIT_OPT_SET_MWINDOW_SIZE),
    WindowMappedLimit(git_libgit2_opt_t.GIT_OPT_GET_MWINDOW_MAPPED_LIMIT, git_libgit2_opt_t.GIT_OPT_SET_MWINDOW_MAPPED_LIMIT),
    SearchPath(git_libgit2_opt_t.GIT_OPT_GET_SEARCH_PATH, git_libgit2_opt_t.GIT_OPT_SET_SEARCH_PATH),
    TemplatePath(git_libgit2_opt_t.GIT_OPT_GET_TEMPLATE_PATH, git_libgit2_opt_t.GIT_OPT_SET_TEMPLATE_PATH),
    UserAgent(git_libgit2_opt_t.GIT_OPT_SET_USER_AGENT, git_libgit2_opt_t.GIT_OPT_GET_USER_AGENT),
    WindowsShareMode(git_libgit2_opt_t.GIT_OPT_SET_WINDOWS_SHAREMODE, git_libgit2_opt_t.GIT_OPT_GET_WINDOWS_SHAREMODE),
    PackMaxObjects(git_libgit2_opt_t.GIT_OPT_SET_PACK_MAX_OBJECTS, git_libgit2_opt_t.GIT_OPT_GET_PACK_MAX_OBJECTS),
    WindowFileLimit(git_libgit2_opt_t.GIT_OPT_GET_MWINDOW_FILE_LIMIT, git_libgit2_opt_t.GIT_OPT_SET_MWINDOW_FILE_LIMIT),
    Extensions(git_libgit2_opt_t.GIT_OPT_SET_EXTENSIONS, git_libgit2_opt_t.GIT_OPT_GET_EXTENSIONS),
    OwnerValidation(git_libgit2_opt_t.GIT_OPT_SET_OWNER_VALIDATION, git_libgit2_opt_t.GIT_OPT_GET_OWNER_VALIDATION),

    CacheObjectLimit(git_libgit2_opt_t.GIT_OPT_SET_CACHE_OBJECT_LIMIT),
    CacheMaxSize(git_libgit2_opt_t.GIT_OPT_SET_CACHE_MAX_SIZE),
    EnableCaching(git_libgit2_opt_t.GIT_OPT_ENABLE_CACHING),
    CachedMemory(git_libgit2_opt_t.GIT_OPT_GET_CACHED_MEMORY),
    SSLCertLocations(git_libgit2_opt_t.GIT_OPT_SET_SSL_CERT_LOCATIONS),
    EnableStrictObjectCreation(git_libgit2_opt_t.GIT_OPT_ENABLE_STRICT_OBJECT_CREATION),
    EnableStrictREFCreation(git_libgit2_opt_t.GIT_OPT_ENABLE_STRICT_SYMBOLIC_REF_CREATION),
    SSLCiphers(git_libgit2_opt_t.GIT_OPT_SET_SSL_CIPHERS),
    EnableOFSDelta(git_libgit2_opt_t.GIT_OPT_ENABLE_OFS_DELTA),
    EnableFSyncGitDir(git_libgit2_opt_t.GIT_OPT_ENABLE_FSYNC_GITDIR),
    EnableStrictHashVerification(git_libgit2_opt_t.GIT_OPT_ENABLE_STRICT_HASH_VERIFICATION),
    @Deprecated("Not implement yet")
    Allocator(git_libgit2_opt_t.GIT_OPT_SET_ALLOCATOR),
    EnableUnsavedIndexSafety(git_libgit2_opt_t.GIT_OPT_ENABLE_UNSAVED_INDEX_SAFETY),
    DisablePackKeepFileChecks(git_libgit2_opt_t.GIT_OPT_DISABLE_PACK_KEEP_FILE_CHECKS),
    EnableHttpExpectContinue(git_libgit2_opt_t.GIT_OPT_ENABLE_HTTP_EXPECT_CONTINUE),
    OdbPackedPriority(git_libgit2_opt_t.GIT_OPT_SET_ODB_PACKED_PRIORITY),
    OdbLoosePriority(git_libgit2_opt_t.GIT_OPT_SET_ODB_LOOSE_PRIORITY),
}
