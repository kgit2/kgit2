package com.kgit2.attr

import com.kgit2.common.callback.CallbackResult
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.toKString
import libgit2.git_attr_foreach_cb

/**
 * @return [CallbackResult.Ok] for continue, [CallbackResult.Custom] for abort
 */
typealias AttrForeachCallback = (name: String, value: String?) -> CallbackResult

interface AttrForeachCallbackPayload {
    var attrForeachCallback: AttrForeachCallback?
}

val staticAttrForeachCallback: git_attr_foreach_cb = staticCFunction {
        name: CPointer<ByteVar>?, value: CPointer<ByteVar>?, payload: COpaquePointer?,
    ->
    val callbackPayload = payload?.asStableRef<AttrForeachCallbackPayload>()?.get()
    callbackPayload?.attrForeachCallback?.invoke(name!!.toKString(), value?.toKString())?.value ?: CallbackResult.Ok.value
}
