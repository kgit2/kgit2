package com.kgit2.attr

import com.kgit2.common.error.GitErrorCode
import kotlinx.cinterop.*
import libgit2.git_attr_foreach_cb

typealias AttrForeachCallback = (name: String, value: String?) -> GitErrorCode

interface AttrForeachCallbackPayload {
    var attrForeachCallback: AttrForeachCallback?
}

val staticAttrForeachCallback: git_attr_foreach_cb = staticCFunction {
        name: CPointer<ByteVar>?, value: CPointer<ByteVar>?, payload: COpaquePointer?,
    ->
    val callbackPayload = payload?.asStableRef<AttrForeachCallbackPayload>()?.get()
    callbackPayload?.attrForeachCallback?.invoke(name!!.toKString(), value?.toKString())?.value ?: GitErrorCode.Ok.value
}
