package com.kgit2.remote

import cnames.structs.git_remote
import com.kgit2.callback.payload.IndexerProgress
import com.kgit2.common.error.errorCheck
import com.kgit2.common.error.toBoolean
import com.kgit2.common.error.toInt
import com.kgit2.common.memory.Memory
import com.kgit2.common.memory.memoryScoped
import com.kgit2.common.option.mutually.AutoTagOption
import com.kgit2.fetch.Direction
import com.kgit2.fetch.FetchOptions
import com.kgit2.memory.Raw
import com.kgit2.memory.GitBase
import com.kgit2.model.toKString
import com.kgit2.model.toList
import com.kgit2.model.withGitBuf
import com.kgit2.model.withGitStrArray
import com.kgit2.proxy.ProxyOptions
import com.kgit2.repository.Repository
import kotlinx.cinterop.*
import libgit2.*

typealias RemotePointer = CPointer<git_remote>

typealias RemoteSecondaryPointer = CPointerVar<git_remote>

typealias RemoteInitial = RemoteSecondaryPointer.(Memory) -> Unit

class RemoteRaw(
    memory: Memory,
    handler: RemotePointer,
) : Raw<git_remote>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: RemoteSecondaryPointer = memory.allocPointerTo(),
        initial: RemoteInitial? = null,
    ) : this(memory, handler.apply {
        runCatching {
            initial?.invoke(handler, memory)
        }.onFailure {
            git_remote_free(handler.value!!)
            memory.free()
        }.getOrThrow()
    }.value!!)

    override val beforeFree: () -> Unit = {
        git_remote_free(handler)
    }
}

class Remote(raw: RemoteRaw) : GitBase<git_remote, RemoteRaw>(raw) {
    constructor(memory: Memory, handler: RemotePointer) : this(RemoteRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        handler: RemoteSecondaryPointer = memory.allocPointerTo(),
        initial: RemoteInitial? = null,
    ) : this(RemoteRaw(memory, handler, initial))

    /**
     * @param repository if null, will create a detached remote
     * @param fetch if nonnull, will create remote with the given fetch refspec
     * @param name if null, will create an anonymous remote, else will create a named remote
     */
    constructor(
        url: String,
        repository: Repository? = null,
        fetch: String? = null,
        name: String? = null,
    ) : this(initial = {
        when {
            repository == null -> git_remote_create_detached(this.ptr, url)
            fetch != null -> git_remote_create_with_fetchspec(this.ptr, repository.raw.handler, name, url, fetch)
            name != null -> git_remote_create(this.ptr, repository.raw.handler, name, url)
            else -> git_remote_create_anonymous(this.ptr, repository.raw.handler, url)
        }.errorCheck()
    })

    val name: String = git_remote_name(raw.handler)!!.toKString()

    val url: String = git_remote_url(raw.handler)!!.toKString()

    var pushUrl: String = git_remote_pushurl(raw.handler)!!.toKString()

    val defaultBranch: String = withGitBuf { buf ->
        git_remote_default_branch(buf, raw.handler).errorCheck()
        buf.toKString()!!
    }

    companion object {
        fun isValidName(remoteName: String): Boolean {
            return git_remote_is_valid_name(remoteName).toBoolean()
        }
    }

    fun connect(direction: Direction, callbacks: RemoteCallbacks? = null, proxy: ProxyOptions? = null) {
        git_remote_connect(
            raw.handler,
            direction.value,
            callbacks?.raw?.handler,
            proxy?.raw?.handler,
            null
        ).errorCheck()
    }

    fun connected(): Boolean {
        return git_remote_connected(raw.handler).toBoolean()
    }

    fun disconnect() {
        git_remote_disconnect(raw.handler).errorCheck()
    }

    fun download(refspecs: List<String>, option: FetchOptions? = null) {
        memoryScoped {
            val refspecsArray = alloc<git_strarray>()
            refspecsArray.strings = refspecs.toCStringArray(this)
            refspecsArray.count = refspecs.size.convert()
            git_remote_download(raw.handler, refspecsArray.ptr, option?.raw?.handler).errorCheck()
        }
    }

    fun stop() {
        git_remote_stop(raw.handler).errorCheck()
    }

    fun refspecs(): MutableList<Refspec> {
        val count = git_remote_refspec_count(raw.handler)
        return MutableList(count.convert()) { index ->
            val refspec = git_remote_get_refspec(raw.handler, index.convert())!!
            Refspec(Memory(), refspec)
        }
    }

    fun fetch(refspecs: List<String>, option: FetchOptions? = null, reflogMessage: String? = null) {
        withGitStrArray { refspecsArray ->
            git_remote_fetch(raw.handler, refspecsArray, option?.raw?.handler, reflogMessage).errorCheck()
        }
    }

    fun updateTips(
        callbacks: RemoteCallbacks,
        updateFetched: Boolean,
        downloadTags: AutoTagOption,
        message: String? = null,
    ) {
        git_remote_update_tips(
            raw.handler,
            callbacks.raw.handler,
            updateFetched.toInt(),
            downloadTags.value,
            message
        ).errorCheck()
    }

    fun push(refspecs: Collection<String>, option: PushOptions? = null) {
        withGitStrArray { refspecsArray ->
            git_remote_push(raw.handler, refspecsArray, option?.raw?.handler).errorCheck()
        }
    }

    fun stats(): IndexerProgress {
        val indexerProgress = git_remote_stats(raw.handler)!!
        return IndexerProgress.fromHandler(indexerProgress.pointed)
    }

    fun list(): List<RemoteHead> {
        val memory = Memory()
        val remoteHead = memory.allocPointerTo<RemoteHeadSecondaryPointer>()
        val size = memory.alloc<ULongVar>()
        git_remote_ls(remoteHead.ptr, size.ptr, raw.handler).errorCheck()
        return List(size.value.convert()) { i ->
            RemoteHead(Memory(), remoteHead.value!![i]!!)
        }
    }

    fun prune(callbacks: RemoteCallbacks) {
        git_remote_prune(raw.handler, callbacks.raw.handler).errorCheck()
    }

    fun fetchRefspecs(): List<String> {
        return withGitStrArray { refspecsArray ->
            git_remote_get_fetch_refspecs(refspecsArray, raw.handler).errorCheck()
            refspecsArray.toList()
        }
    }

    fun pushRefspecs(): List<String> {
        return withGitStrArray { refspecsArray ->
            git_remote_get_push_refspecs(refspecsArray, raw.handler).errorCheck()
            refspecsArray.toList()
        }
    }
}
