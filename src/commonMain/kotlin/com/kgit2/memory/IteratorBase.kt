package com.kgit2.memory

import com.kgit2.common.error.GitError
import com.kgit2.common.error.GitErrorCode
import com.kgit2.memory.collections.NativeMutableList
import kotlinx.atomicfu.atomic
import kotlinx.cinterop.CPointed

abstract class IteratorBase<T : CPointed, R : Raw<T>, E>(raw: R) : GitBase<T, R>(raw), Iterator<E> {
    protected val cache = NativeMutableList<E>()
    protected val index = atomic(-1)

    abstract fun nextRaw(): Result<E>

    override fun hasNext(): Boolean {
        if (index.value < cache.size - 1) {
            return true
        }
        nextRaw().onSuccess {
            cache.add(it)
        }.onFailure {
            if (it !is GitError || it.code != GitErrorCode.IterOver) {
                throw it
            }
            return false
        }
        return true
    }

    override fun next(): E = cache[index.incrementAndGet()]

    val list by lazy { asSequence().toList() }
}
