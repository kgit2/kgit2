package com.floater.git.common.option

import libgit2.git_checkout_notify_t

enum class CheckoutNotificationType(val value: git_checkout_notify_t) {
    GIT_CHECKOUT_NOTIFY_NONE(0u),

    /**
     * Invokes checkout on conflicting paths.
     */
    GIT_CHECKOUT_NOTIFY_CONFLICT(1u shl 0),

    /**
     * Notifies about "dirty" files, i.e. those that do not need an update
     * but no longer match the baseline.  Core git displays these files when
     * checkout runs, but won't stop the checkout.
     */
    GIT_CHECKOUT_NOTIFY_DIRTY(1u shl 1),

    /**
     * Sends notification for any file changed.
     */
    GIT_CHECKOUT_NOTIFY_UPDATED(1u shl 2),

    /**
     * Notifies about untracked files.
     */
    GIT_CHECKOUT_NOTIFY_UNTRACKED(1u shl 3),

    /**
     * Notifies about ignored files.
     */
    GIT_CHECKOUT_NOTIFY_IGNORED(1u shl 4),

    GIT_CHECKOUT_NOTIFY_ALL(0x0FFFFu),
}
