package com.kgit2.memory

import kotlinx.atomicfu.atomic
import kotlinx.cinterop.CPointed

abstract class IterableBase<T : CPointed, R : Raw<T>, E>(raw: R) : GitBase<T, R>(raw), Iterable<E> {
    abstract val size: Long

    abstract operator fun get(index: Long): E

    override fun iterator(): Iterator<E> = InnerIterator()

    open inner class InnerIterator : Iterator<E> {
        val index = atomic(-1L)
        override fun hasNext(): Boolean = index.value < (size - 1)
        override fun next(): E = get(index.incrementAndGet())
    }
}
