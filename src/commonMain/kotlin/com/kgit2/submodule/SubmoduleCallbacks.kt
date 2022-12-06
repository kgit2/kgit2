package com.kgit2.submodule

import cnames.structs.git_submodule
import com.kgit2.common.callback.CallbackResult
import com.kgit2.common.memory.Memory
import kotlinx.cinterop.*
import libgit2.git_submodule_cb

/**
 * Function pointer to receive each submodule
 *
 * @param submodule git_submodule currently being visited
 * @param name name of the submodule
 * @return 0 on success or error code
 */
typealias SubmoduleCallback = (submodule: Submodule, name: String) -> CallbackResult

interface SubmoduleCallbackPayload {
    var submoduleCallback: SubmoduleCallback?
}

val staticSubmoduleCallback: git_submodule_cb = staticCFunction {
        submodule: CPointer<git_submodule>?,
        name: CPointer<ByteVar>?,
        payload: COpaquePointer?,
    ->
    val callback = payload?.asStableRef<SubmoduleCallbackPayload>()?.get()
    callback?.submoduleCallback?.invoke(
        Submodule(Memory(), submodule!!),
        name!!.toKString()
    )?.value ?: CallbackResult.Ok.value
}
