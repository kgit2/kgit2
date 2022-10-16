package com.floater.git.repository

import com.floater.git.checkout.CheckoutBuilder
import com.floater.git.common.option.CloneLocalOpts
import com.floater.git.common.option.FetchOptions

open class RepositoryBuilder {
    open var bare = false
    open var branch: String? = null
    open var local = false
    open var hardlinks = true
    // TODO()
//    open var checkout: Option<CheckoutBuilder<'cb>>,
//    open var fetch_opts: Option<FetchOptions<'cb>>,
//    open var cloneLocalOpts: CloneLocalOpts? = null
//    open var remote_create: Option<Box<RemoteCreate<'cb>>>,
}
