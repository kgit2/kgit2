package com.kgit2.memory

import com.kgit2.common.error.KGitException
import kotlinx.cinterop.CPointed

abstract class MutableIteratorBase<T : CPointed, R : Raw<T>, E>(raw: R) : IteratorBase<T, R, E>(raw), MutableIterator<E> {
    abstract fun removeRaw(): Result<Unit>

    /**
     * Removes from the underlying collection the last element returned by this iterator.
     */
    override fun remove() {
        if (index.value < 0) {
            throw KGitException("next() has not been called, current index is ${index.value}")
        }
        when {
            index.value < 0 -> throw KGitException("next() has not been called, current index is ${index.value}")
            index.value >= cache.size -> throw KGitException("remove() has already been called, current index is ${index.value}")
            else -> {
                removeRaw().onSuccess {
                    cache.removeAt(index.getAndDecrement())
                }.onFailure { throw it }
            }
        }
    }

}
