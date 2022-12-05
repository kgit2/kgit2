package com.kgit2.common.callback

open class CallbackResult(val value: Int) {
    companion object {
        val Ok = CallbackResult(0)
        val Abort = CallbackResult(-1)
        val Skip = CallbackResult(1)
    }
    class Custom(value: Int) : CallbackResult(value)
}
