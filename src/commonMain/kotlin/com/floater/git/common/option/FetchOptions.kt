package com.floater.git.common.option

import com.floater.git.common.callback.RemoteCallbacks
import kotlinx.cinterop.internal.CCall

open class FetchOptions {
    //    open var callbacks: Option<RemoteCallbacks<'cb>>
    open var proxy: ProxyOptions? = null
    open var prune: FetchPrune = FetchPrune.GIT_FETCH_PRUNE_UNSPECIFIED
    open var updateFetchHead = false
    open var downloadTags: AutoTagOption = AutoTagOption.GIT_REMOTE_DOWNLOAD_TAGS_UNSPECIFIED
    open var followRedirects: RemoteRedirect = RemoteRedirect.GIT_REMOTE_DOWNLOAD_TAGS_INITIAL
    open var customHeaders = mutableListOf<String>()
}

enum class FetchPrune {
    GIT_FETCH_PRUNE_UNSPECIFIED,
    GIT_FETCH_NO_PRUNE,
    GIT_FETCH_OFF_PRUNE,
}

enum class AutoTagOption {
    GIT_REMOTE_DOWNLOAD_TAGS_UNSPECIFIED,
    GIT_REMOTE_DOWNLOAD_TAGS_AUTO,
    GIT_REMOTE_DOWNLOAD_TAGS_NONE,
    GIT_REMOTE_DOWNLOAD_TAGS_ALL,
}

enum class RemoteRedirect {
    GIT_REMOTE_DOWNLOAD_TAGS_INITIAL,
    GIT_REMOTE_DOWNLOAD_TAGS_NONE,
    GIT_REMOTE_DOWNLOAD_TAGS_ALL,
}
