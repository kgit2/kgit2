package com.kgit2.repository

import com.kgit2.annotations.Raw
import com.kgit2.callback.RemoteCreateCallback
import com.kgit2.callback.RepositoryCreateCallback
import com.kgit2.checkout.CheckoutOptions
import com.kgit2.common.error.toBoolean
import com.kgit2.common.error.toInt
import com.kgit2.common.memory.Memory
import com.kgit2.fetch.FetchOptions
import com.kgit2.memory.GitBase
import com.kgit2.remote.Remote
import kotlinx.cinterop.*
import libgit2.git_clone_options

@Raw(
    base = "git_clone_options",
    secondaryPointer = false,
)
class CloneOptions(raw: CloneOptionsRaw = CloneOptionsRaw()) : GitBase<git_clone_options, CloneOptionsRaw>(raw) {
    constructor(
        memory: Memory = Memory(),
        handler: CloneOptionsPointer = memory.alloc<git_clone_options>().ptr,
        initial: CloneOptionsInitial?,
    ) : this(CloneOptionsRaw(memory, handler, initial))

    val checkoutOptions: CheckoutOptions = CheckoutOptions(Memory(), raw.handler.pointed.checkout_opts.ptr)

    val fetchOptions: FetchOptions = FetchOptions(Memory(), raw.handler.pointed.fetch_opts.ptr)

    var bare: Boolean = false
        set(value) {
            raw.handler.pointed.bare = value.toInt()
            field = value
        }

    var local: CloneLocalOpts = CloneLocalOpts.fromRaw(raw.handler.pointed.local)
        set(value) {
            raw.handler.pointed.local = value.value
            field = value
        }

    var checkoutBranch: String? = raw.handler.pointed.checkout_branch?.toKString()
        set(value) {
            raw.handler.pointed.checkout_branch = value?.cstr?.getPointer(raw.memory)
            field = value
        }

    var repositoryCreateCallback: RepositoryCreateCallback? = null
        set(value) {
            field = value
            raw.handler.pointed.repository_cb_payload = StableRef.create(value as Any).asCPointer()
            raw.handler.pointed.repository_cb = staticCFunction { repo, path, bare, payload ->
                val callback = payload!!.asStableRef<RepositoryCreateCallback>().get()
                callback.repositoryCreate(
                    Repository(Memory(), repo!!.pointed.value!!),
                    path!!.toKString(),
                    bare.toBoolean(),
                )
            }
        }

    var remoteCreateCallback: RemoteCreateCallback? = null
        set(value) {
            field = value
            raw.handler.pointed.remote_cb_payload = StableRef.create(value as Any).asCPointer()
            raw.handler.pointed.remote_cb = staticCFunction { remote, repo, name, url, payload ->
                val callback = payload!!.asStableRef<RemoteCreateCallback>().get()
                callback.remoteCreate(
                    Remote(Memory(), remote!!.pointed.value!!),
                    Repository(Memory(), repo!!),
                    name!!.toKString(),
                    url!!.toKString(),
                ).value
            }
        }
}
