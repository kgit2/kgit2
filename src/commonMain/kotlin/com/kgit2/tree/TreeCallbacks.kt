package com.kgit2.tree

import cnames.structs.git_tree_entry
import com.kgit2.common.callback.CallbackResult
import kotlinx.cinterop.*
import libgit2.*

/**
 * Function pointer to receive each entry in a tree
 *
 * @param root The root of the tree
 * @param entry The current entry
 * @return 0 on success or error code
 */
typealias TreeWalkCallback = (root: String, entry: TreeEntry) -> CallbackResult

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
    )?.value ?: CallbackResult.Ok.value
}

typealias TreebuilderFilterCallback = (entry: TreeEntry) -> CallbackResult

interface TreebuilderFilterCallbackPayload {
    var treebuilderFilterCallback: TreebuilderFilterCallback?
}

val staticTreebuilderFilterCallback: git_treebuilder_filter_cb = staticCFunction {
        treeEntry: CPointer<git_tree_entry>?, payload: COpaquePointer?,
    ->
    val callbackPayload = payload?.asStableRef<TreebuilderFilterCallbackPayload>()?.get()
    callbackPayload?.treebuilderFilterCallback?.invoke(TreeEntry(handler = treeEntry!!))?.value ?: CallbackResult.Ok.value
}
