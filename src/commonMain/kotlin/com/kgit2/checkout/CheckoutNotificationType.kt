package com.kgit2.checkout

import libgit2.*

enum class CheckoutNotificationType(val value: git_checkout_notify_t) {
    None(GIT_CHECKOUT_NOTIFY_NONE),

    /**
     * Invokes checkout on conflicting paths.
     */
    Conflict(GIT_CHECKOUT_NOTIFY_CONFLICT),

    /**
     * Notifies about "dirty" files, i.e. those that do not need an update
     * but no longer match the baseline.  Core git displays these files when
     * checkout runs, but won't stop the checkout.
     */
    NotifyDirty(GIT_CHECKOUT_NOTIFY_DIRTY),

    /**
     * Sends notification for any file changed.
     */
    Update(GIT_CHECKOUT_NOTIFY_UPDATED),

    /**
     * Notifies about untracked files.
     */
    UnTracked(GIT_CHECKOUT_NOTIFY_UNTRACKED),

    /**
     * Notifies about ignored files.
     */
    Ignored(GIT_CHECKOUT_NOTIFY_IGNORED),

    All(GIT_CHECKOUT_NOTIFY_ALL);

    companion object {
        fun fromRaw(value: git_checkout_notify_t): CheckoutNotificationType {
            return when (value) {
                GIT_CHECKOUT_NOTIFY_NONE -> None
                GIT_CHECKOUT_NOTIFY_CONFLICT -> Conflict
                GIT_CHECKOUT_NOTIFY_DIRTY -> NotifyDirty
                GIT_CHECKOUT_NOTIFY_UPDATED -> Update
                GIT_CHECKOUT_NOTIFY_UNTRACKED -> UnTracked
                GIT_CHECKOUT_NOTIFY_IGNORED -> Ignored
                GIT_CHECKOUT_NOTIFY_ALL -> All
                else -> error("Unknown checkout notification type: $value")
            }
        }
    }
}
