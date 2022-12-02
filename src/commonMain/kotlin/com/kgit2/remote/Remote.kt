package com.kgit2.remote

import cnames.structs.git_remote
import com.kgit2.annotations.Raw
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.extend.toBoolean
import com.kgit2.common.extend.toInt
import com.kgit2.common.memory.Memory
import com.kgit2.common.memory.memoryScoped
import com.kgit2.common.option.mutually.AutoTagOption
import com.kgit2.fetch.Direction
import com.kgit2.fetch.FetchOptions
import com.kgit2.index.IndexerProgress
import com.kgit2.memory.RawWrapper
import com.kgit2.model.*
import com.kgit2.proxy.ProxyOptions
import com.kgit2.repository.Repository
import kotlinx.cinterop.*
import libgit2.*

@Raw(
    base = git_remote::class,
    free = "git_remote_free",
)
class Remote(raw: RemoteRaw) : RawWrapper<git_remote, RemoteRaw>(raw) {
    constructor(memory: Memory, handler: RemotePointer) : this(RemoteRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        secondary: RemoteSecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: RemoteSecondaryInitial? = null,
    ) : this(RemoteRaw(memory, secondary, secondaryInitial))

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
    ) : this(secondaryInitial = {
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

    val defaultBranch: Buf = Buf {
        git_remote_default_branch(this, raw.handler).errorCheck()
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

    fun fetch(refSpecs: Collection<String>, option: FetchOptions? = null, reflogMessage: String? = null) =
        git_remote_fetch(raw.handler, refSpecs.toStrArray().raw.handler, option?.raw?.handler, reflogMessage).errorCheck()

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

    fun push(refSpecs: Collection<String>, option: PushOptions? = null) =
        git_remote_push(raw.handler, refSpecs.toStrArray().raw.handler, option?.raw?.handler).errorCheck()

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

    fun fetchRefspecs(): StrArray = StrArray(StrarrayRaw(initial = {
        git_remote_get_fetch_refspecs(this, raw.handler).errorCheck()
    }))

    fun pushRefspecs(): StrArray = StrArray(StrarrayRaw(initial = {
        git_remote_get_push_refspecs(this, raw.handler).errorCheck()
    }))
}
