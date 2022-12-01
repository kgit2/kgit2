package com.kgit2.repository

import cnames.structs.git_repository
import com.kgit2.common.error.GitErrorCode
import com.kgit2.common.extend.toBoolean
import com.kgit2.common.memory.Memory
import com.kgit2.oid.Oid
import com.kgit2.remote.Remote
import kotlinx.cinterop.*
import libgit2.*

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

interface RepositoryCreateCallbackPayload {
    var repositoryCreateCallback: RepositoryCreateCallback?
}

val staticRepositoryCreateCallback: git_repository_create_cb = staticCFunction {
        repo: CPointer<CPointerVar<git_repository>>?,
        path: CPointer<ByteVar>?,
        bare: Int,
        payload: COpaquePointer?,
    ->
    val callback = payload?.asStableRef<RepositoryCreateCallbackPayload>()?.get()
    callback?.repositoryCreateCallback?.invoke(
        Repository(Memory(), repo!!.pointed.value!!),
        path!!.toKString(),
        bare.toBoolean(),
    )?.value ?: GitErrorCode.Ok.value
}

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

interface RemoteCreateCallbackPayload {
    var remoteCreateCallback: RemoteCreateCallback?
}

val staticRemoteCreateCallback: git_remote_create_cb = staticCFunction {
        remote: CPointer<CPointerVar<git_remote>>?,
        repository: CPointer<git_repository>?,
        name: CPointer<ByteVar>?,
        url: CPointer<ByteVar>?,
        payload: COpaquePointer?,
    ->
    val callback = payload?.asStableRef<CloneOptions.CallbacksPayload>()?.get()
    callback?.remoteCreateCallback?.invoke(
            Remote(Memory(), remote!!.pointed.value!!),
            Repository(Memory(), repository!!),
            name!!.toKString(),
            url!!.toKString(),
        )?.value ?: GitErrorCode.Ok.value
}

typealias RepositoryFetchHeadForeachCallback = (refname: String, remoteUrl: String, oid: Oid, isMerge: Boolean) -> GitErrorCode

interface RepositoryFetchHeadForeachCallbackPayload {
    var repositoryFetchHeadForeachCallback: RepositoryFetchHeadForeachCallback?
}

val staticRepositoryFetchHeadForeachCallback: git_repository_fetchhead_foreach_cb = staticCFunction {
        refname: CPointer<ByteVar>?,
        remoteUrl: CPointer<ByteVar>?,
        oid: CPointer<git_oid>?,
        isMerge: UInt,
        payload: COpaquePointer?
    ->
    val callback = payload?.asStableRef<RepositoryFetchHeadForeachCallbackPayload>()?.get()
    callback?.repositoryFetchHeadForeachCallback?.invoke(
        refname!!.toKString(),
        remoteUrl!!.toKString(),
        Oid(handler = oid!!),
        isMerge.convert<Int>().toBoolean(),
    )?.value ?: GitErrorCode.Ok.value
}
