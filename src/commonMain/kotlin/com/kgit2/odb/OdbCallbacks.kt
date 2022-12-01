package com.kgit2.odb

import com.kgit2.checkout.IndexerProgressCallback
import com.kgit2.common.error.GitErrorCode
import com.kgit2.common.memory.Memory
import com.kgit2.oid.Oid
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.staticCFunction
import libgit2.git_odb_foreach_cb
import libgit2.git_oid

typealias OdbForEachCallback = (Oid) -> GitErrorCode

interface OdbForEachCallbackPayload {
    var odbForEachCallback: OdbForEachCallback?
}

val staticOdbForEachCallback: git_odb_foreach_cb = staticCFunction {
        oid: CPointer<git_oid>?,
        payload: COpaquePointer?,
    ->
    val callback = payload!!.asStableRef<OdbForEachCallbackPayload>().get()
    callback.odbForEachCallback!!.invoke(Oid(Memory(), oid!!)).value
}
