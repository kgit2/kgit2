package com.floater.git.common.callback

import com.floater.git.common.option.CheckoutNotificationType

interface Notify {
    fun notify(
        type: CheckoutNotificationType,
        path: String?,
        /* TODO(baseline diff) TODO(target diff) TODO(workdir diff) */
    )
}
