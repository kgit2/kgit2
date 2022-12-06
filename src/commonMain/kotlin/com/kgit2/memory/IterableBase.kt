package com.kgit2.memory

import kotlinx.atomicfu.atomic

interface IterableBase<E> : Iterable<E> {
    val size: Long

    operator fun get(index: Long): E

    override fun iterator(): Iterator<E> = InnerIterator(this)

    open class InnerIterator<E>(val owner: IterableBase<E>) : Iterator<E> {
        val index = atomic(-1L)
        override fun hasNext(): Boolean = index.value < (owner.size - 1)
        override fun next(): E = owner[index.incrementAndGet()]
    }
}
