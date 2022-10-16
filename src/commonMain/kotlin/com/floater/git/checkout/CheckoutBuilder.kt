package com.floater.git.checkout

import com.floater.git.common.callback.Notify
import com.floater.git.common.option.CheckoutNotificationType
import com.floater.git.common.option.CheckoutStrategyOpts
import com.floater.git.common.callback.Progress

open class CheckoutBuilder {
    open var theirLabel: String? = null
    open var ourLabel: String? = null
    open var ancestorLabel: String? = null
    open var targetDir: String? = null
    open var paths: MutableList<String> = mutableListOf()
    open var filePerm: Int? = null
    open var dirPerm: Int? = null
    open var disableFilters = false
    open var checkoutStrategyOpts: CheckoutStrategyOpts = CheckoutStrategyOpts.GIT_CHECKOUT_SAFE
    open var progress: Progress? = null
    open var notify: Notify? = null
    open var notifyFlags: CheckoutNotificationType = CheckoutNotificationType.GIT_CHECKOUT_NOTIFY_NONE
}
