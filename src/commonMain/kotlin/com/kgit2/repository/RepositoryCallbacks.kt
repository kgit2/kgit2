package com.kgit2.repository

import com.kgit2.common.error.GitErrorCode
import com.kgit2.remote.Remote

/**
 * The signature of a function matching git_repository_init, with an
 * additional void * as callback payload.
 *
 * Callers of git_clone my provide a function matching this signature
 * to override the repository creation and customization process
 * during a clone operation.
 *
 * @param repository the resulting repository
 * @param path path in which to create the repository
 * @param bare whether the repository is bare. This is the value from the clone options
 * @return 0, or a negative value to indicate error
 */
typealias RepositoryCreateCallback = (repository: Repository, path: String, bare: Boolean) -> GitErrorCode

/**
 * The signature of a function matching git_remote_create, with an additional
 * void* as a callback payload.
 *
 * Callers of git_clone may provide a function matching this signature to override
 * the remote creation and customization process during a clone operation.
 *
 * @param remote the resulting remote
 * @param repository the repository in which to create the remote
 * @param name the remote's name
 * @param url the remote's url
 * @return GitErrorCode GIT_OK on success, GIT_EINVALIDSPEC, GIT_EEXISTS or an error code
 */
typealias RemoteCreateCallback = (remote: Remote, repository: Repository, name: String, url: String) -> GitErrorCode
