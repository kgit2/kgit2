package com.kgit2.repository

import com.kgit2.callback.RemoteCreateCallback
import com.kgit2.checkout.CheckoutBuilder
import com.kgit2.fetch.FetchOptions

open class RepositoryBuilder {
    open var bare = false
    open var branch: String? = null
    open var local = false
    open var hardlinks = true
    open var checkoutBuilder: CheckoutBuilder? = null
    open var fetchOptions: FetchOptions? = null
    open var cloneLocalOpts: CloneLocalOpts? = null
    open var remoteCreate: RemoteCreateCallback? = null
}
