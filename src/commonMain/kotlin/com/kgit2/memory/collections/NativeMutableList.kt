package com.kgit2.memory.collections

import kotlin.native.concurrent.AtomicReference

/**
 * Helper class to allow to have a [MutableList] inside a frozen native object.
 * It uses an [AtomicReference] of [List] underneath and sets a new one at every mutation
 *
 * [Iterator]s and [subList] will return a snapshot of current data and changes won't be reflected
 */
class NativeMutableList<E> private constructor(
    private val ref: AtomicReference<List<E>>,
) : MutableList<E>, List<E> by ref.value {

    constructor() : this(emptyList())

    constructor(list: List<E>) : this(AtomicReference(list))

    override fun clear() {
        ref.compareAndSwap(ref.value, emptyList())
    }

    override fun get(index: Int): E = ref.value[index]

    override fun add(element: E): Boolean = ref.compareAndSet(ref.value, ref.value + element)

    override fun add(index: Int, element: E): Unit {
        ref.compareAndSet(ref.value, ref.value.take(index) + element + ref.value.subList(index, size))
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean =
        ref.compareAndSet(ref.value, ref.value.take(index) + elements + ref.value.subList(index, size))

    override fun addAll(elements: Collection<E>): Boolean =
        ref.compareAndSet(ref.value, ref.value + elements)

    override fun remove(element: E): Boolean =
        ref.compareAndSwap(ref.value, ref.value - element).size != size


    override fun removeAll(elements: Collection<E>): Boolean =
        ref.compareAndSwap(ref.value, ref.value - elements.toSet()).size != size

    override fun removeAt(index: Int): E =
        ref.compareAndSwap(ref.value, ref.value.take(index) + ref.value.subList(index + 1, size))[index]

    override fun retainAll(elements: Collection<E>): Boolean =
        ref.compareAndSwap(ref.value, ref.value.filter { it in elements }).size != size

    override fun set(index: Int, element: E): E =
        ref.compareAndSwap(ref.value, ref.value.take(index) + element + ref.value.subList(index + 1, size))[index]

    override fun iterator() = toMutableList().iterator()

    override fun listIterator() = toMutableList().listIterator()

    override fun listIterator(index: Int) = toMutableList().listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int) =
        ref.value.subList(fromIndex, toIndex).toMutableList()

    override fun hashCode() = ref.value.hashCode()

    override fun equals(other: Any?) = ref.value == (other as? NativeMutableList<*>)?.ref?.value

    override fun toString() = ref.value.toString()
}
