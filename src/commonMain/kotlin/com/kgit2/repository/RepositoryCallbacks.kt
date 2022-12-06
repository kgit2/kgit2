package com.kgit2.repository

import cnames.structs.git_remote
import cnames.structs.git_repository
import com.kgit2.common.callback.CallbackResult
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
typealias RepositoryCreateCallback = (path: String, bare: Boolean) -> Pair<Repository?, CallbackResult>

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
    val result = callback?.repositoryCreateCallback?.invoke(
        path!!.toKString(),
        bare.toBoolean(),
    )
    result?.first?.let {
        repo!!.pointed.value = it.raw.handler
    }
    result?.second?.value ?: CallbackResult.Ok.value
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
typealias RemoteCreateCallback = (repository: Repository?, name: String, url: String) -> Pair<Remote?, CallbackResult>

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
    val result = callback?.remoteCreateCallback?.invoke(
        repository?.let { Repository(Memory(), it) },
        name!!.toKString(),
        url!!.toKString(),
    )
    result?.first?.let {
        remote!!.pointed.value = it.raw.handler
    }
    result?.second?.value ?: CallbackResult.Ok.value
}

typealias RepositoryFetchHeadForeachCallback = (refname: String, remoteUrl: String, oid: Oid, isMerge: Boolean) -> CallbackResult

interface RepositoryFetchHeadForeachCallbackPayload {
    var repositoryFetchHeadForeachCallback: RepositoryFetchHeadForeachCallback?
}

val staticRepositoryFetchHeadForeachCallback: git_repository_fetchhead_foreach_cb = staticCFunction {
        refname: CPointer<ByteVar>?,
        remoteUrl: CPointer<ByteVar>?,
        oid: CPointer<git_oid>?,
        isMerge: UInt,
        payload: COpaquePointer?,
    ->
    val callback = payload?.asStableRef<RepositoryFetchHeadForeachCallbackPayload>()?.get()
    callback?.repositoryFetchHeadForeachCallback?.invoke(
        refname!!.toKString(),
        remoteUrl!!.toKString(),
        Oid(handler = oid!!),
        isMerge.convert<Int>().toBoolean(),
    )?.value ?: CallbackResult.Ok.value
}

typealias RepositoryMergeHeadForeachCallback = (oid: Oid) -> CallbackResult

interface RepositoryMergeHeadForeachCallbackPayload {
    var repositoryMergeHeadForeachCallback: RepositoryMergeHeadForeachCallback?
}

val staticRepositoryMergeHeadForeachCallback: git_repository_mergehead_foreach_cb =
    staticCFunction { id: CPointer<git_oid>?, payload: COpaquePointer?
        ->
        val callbackPayload = payload?.asStableRef<RepositoryMergeHeadForeachCallbackPayload>()?.get()
        callbackPayload?.repositoryMergeHeadForeachCallback?.invoke(
            Oid(handler = id!!)
        )?.value ?: CallbackResult.Ok.value
    }


typealias RevwalkHideCallback = (oid: Oid) -> CallbackResult

interface RevwalkHideCallbackPayload {
    var revwalkHideCallback: RevwalkHideCallback?
}

val staticRevwalkHideCallback: git_revwalk_hide_cb = staticCFunction {
        oid: CPointer<git_oid>?, payload: COpaquePointer?,
    ->
    val callbackPayload = payload?.asStableRef<RevwalkHideCallbackPayload>()?.get()
    callbackPayload?.revwalkHideCallback?.invoke(Oid(handler = oid!!))?.value ?: CallbackResult.Ok.value
}
