package com.kgit2.remote

import cnames.structs.git_remote
import com.kgit2.callback.payload.IndexerProgress
import com.kgit2.common.error.errorCheck
import com.kgit2.proxy.ProxyOptions
import com.kgit2.common.option.mutually.AutoTagOption
import com.kgit2.fetch.Direction
import com.kgit2.fetch.FetchOptions
import com.kgit2.model.*
import com.kgit2.repository.Repository
import kotlinx.cinterop.*
import libgit2.*

class Remote(
    override val handler: CPointer<git_remote>,
    override val arena: Arena,
    val name: String,
    val url: String,
    var pushUrl: String,
    val defaultBranch: String,
) : AutoFreeGitBase<CPointer<git_remote>> {
    companion object {
        fun isValidName(remoteName: String): Boolean {
            return git_remote_is_valid_name(remoteName) == 1
        }

        fun new(handler: CPointer<git_remote>, arena: Arena): Remote {
            return Remote(
                handler,
                arena,
                name = git_remote_name(handler)!!.toKString(),
                url = git_remote_url(handler)!!.toKString(),
                pushUrl = git_remote_pushurl(handler)!!.toKString(),
                defaultBranch = withGitBuf { buf ->
                    git_remote_default_branch(buf, handler).errorCheck()
                    buf.toKString()!!
                },
            )
        }

        fun create(repository: Repository, name: String, url: String): Remote {
            val remote = new(
                memScoped {
                    val pointer = allocPointerTo<git_remote>()
                    git_remote_create(pointer.ptr, repository.handler, name, url).errorCheck()
                    pointer.value!!
                },
                repository.arena
            )
            return remote
        }

        fun <T : CharSequence> createDetached(url: T): Remote {
            val arena = Arena()
            val remote = arena.allocPointerTo<git_remote>()
            git_remote_create_detached(remote.ptr, url.toString()).errorCheck()
            return new(remote.value!!, arena)
        }

        fun createAnonymous(repository: Repository, url: String): Remote {
            val arena = Arena()
            val remote = arena.allocPointerTo<git_remote>()
            git_remote_create_anonymous(remote.ptr, repository.handler, url).errorCheck()
            return new(remote.value!!, arena)
        }

        fun createWithFetchSpec(repository: Repository, name: String, url: String, fetch: String): Remote {
            val arena = Arena()
            val remote = arena.allocPointerTo<git_remote>()
            git_remote_create_with_fetchspec(remote.ptr, repository.handler, name, url, fetch).errorCheck()
            return new(remote.value!!, arena)
        }
    }

    override fun free() {
        git_remote_free(handler)
        super.free()
    }

    fun connect(direction: Direction, callbacks: RemoteCallbacks? = null, proxy: ProxyOptions? = null) {
        git_remote_connect(handler, direction.value, callbacks?.handler, proxy?.handler, null).errorCheck()
    }

    fun connected(): Boolean {
        return git_remote_connected(handler) == 1
    }

    fun disconnect() {
        git_remote_disconnect(handler)
    }

    fun download(refspecs: List<String>, option: FetchOptions? = null) {
        autoFreeScoped {
            val refspecsArray = alloc<git_strarray>()
            refspecsArray.strings = refspecs.toCStringArray(this)
            refspecsArray.count = refspecs.size.convert()
            git_remote_download(handler, refspecsArray.ptr, option?.handler).errorCheck()
        }
    }

    fun stop() {
        git_remote_stop(handler).errorCheck()
    }

    fun refspecs(): MutableList<Refspec> {
        val count = git_remote_refspec_count(handler)
        return MutableList(count.convert()) { index ->
            val refspec = git_remote_get_refspec(handler, index.convert())!!
            Refspec(refspec, Arena())
        }
    }

    fun fetch(refspecs: List<String>, option: FetchOptions? = null, reflogMessage: String? = null) {
        withGitStrArray { refspecsArray ->
            git_remote_fetch(handler, refspecsArray, option?.handler, reflogMessage).errorCheck()
        }
    }

    fun updateTips(
        callbacks: RemoteCallbacks,
        updateFetched: Boolean,
        downloadTags: AutoTagOption,
        message: String? = null,
    ) {
        git_remote_update_tips(
            handler,
            callbacks.handler,
            if (updateFetched) 1 else 0,
            downloadTags.value,
            message
        ).errorCheck()
    }

    fun push(refspecs: Collection<String>, option: PushOptions? = null) {
        withGitStrArray { refspecsArray ->
            git_remote_push(handler, refspecsArray, option?.handler).errorCheck()
        }
    }

    fun stats(): IndexerProgress {
        val indexerProgress = git_remote_stats(handler)!!
        return IndexerProgress.fromHandler(indexerProgress.pointed)
    }

    fun list(): List<RemoteHead> {
        val remoteHead = arena.allocPointerTo<CPointerVar<git_remote_head>>()
        val size = arena.alloc<ULongVar>()
        git_remote_ls(remoteHead.ptr, size.ptr, handler).errorCheck()
        return List(size.value.convert()) { i ->
            RemoteHead(remoteHead.value!![i]!!, arena)
        }
    }

    fun prune(callbacks: RemoteCallbacks) {
        git_remote_prune(handler, callbacks.handler).errorCheck()
    }

    fun fetchRefspecs(): List<String> {
        return withGitStrArray { refspecsArray ->
            git_remote_get_fetch_refspecs(refspecsArray, handler).errorCheck()
            refspecsArray.toList()
        }
    }

    fun pushRefspecs(): List<String> {
        return withGitStrArray { refspecsArray ->
            git_remote_get_push_refspecs(refspecsArray, handler).errorCheck()
            refspecsArray.toList()
        }
    }
}
