package com.kgit2.tree

import com.kgit2.common.error.GitErrorCode
import kotlinx.cinterop.*
import libgit2.*

/**
 * Function pointer to receive each entry in a tree
 *
 * @param root The root of the tree
 * @param entry The current entry
 * @return 0 on success or error code
 */
typealias TreeWalkCallback = (root: String, entry: TreeEntry) -> GitErrorCode

interface TreeWalkCallbackPayload {
    var treeWalkCallback: TreeWalkCallback?
}

val staticTreeWalkCallback: git_treewalk_cb = staticCFunction {
        root: CPointer<ByteVar>?,
        entry: CPointer<git_tree_entry>?,
        payload: COpaquePointer?,
    ->
    val callback = payload?.asStableRef<TreeWalkCallbackPayload>()?.get()
    callback?.treeWalkCallback?.invoke(
        root!!.toKString(),
        TreeEntry(handler = entry!!)
    )?.value ?: GitErrorCode.Ok.value
}
