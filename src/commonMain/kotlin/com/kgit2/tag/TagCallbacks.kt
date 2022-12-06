package com.kgit2.tag

import com.kgit2.common.callback.CallbackResult
import com.kgit2.oid.Oid
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.toKString
import libgit2.git_oid
import libgit2.git_tag_foreach_cb

typealias TagForeachCallback = (tagName: String, id: Oid) -> CallbackResult

interface TagForeachCallbackPayload {
    var tagForeachCallback: TagForeachCallback?
}

val staticTagForeachCallback: git_tag_foreach_cb = staticCFunction {
        name: CPointer<ByteVar>?,
        id: CPointer<git_oid>?,
        payload: COpaquePointer?,
    ->
    val callback = payload?.asStableRef<TagForeachCallbackPayload>()?.get()
    callback?.tagForeachCallback?.invoke(
        name!!.toKString(),
        Oid(handler = id!!)
    )?.value ?: CallbackResult.Ok.value
}
