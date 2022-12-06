package com.kgit2.memory

import com.kgit2.common.error.GitError
import com.kgit2.common.error.GitErrorCode
import com.kgit2.concurrency.NativeMutableList
import kotlinx.atomicfu.atomic
import kotlinx.cinterop.CPointed
import kotlin.native.ref.WeakReference

interface IteratorBase<E: Any> : Iterator<E> {
    var next: WeakReference<E>?

    fun nextRaw(): Result<E>

    override fun hasNext(): Boolean {
        nextRaw().onSuccess {
            next = WeakReference(it)
        }.onFailure {
            next?.clear()
            if (it !is GitError || it.code != GitErrorCode.IterOver) {
                throw it
            }
            return false
        }
        return true
    }

    override fun next(): E = next?.get() ?: throw NoSuchElementException()
}
