package com.kgit2.memory

interface MutableListBase<E> : MutableList<E> {
    val innerList: MutableList<E>

    fun updateRaw(list: List<E>)

    fun clearRaw()

    override fun add(element: E): Boolean = innerList.add(element).also { updateRaw(innerList) }

    override fun addAll(elements: Collection<E>): Boolean =
        innerList.addAll(elements).also { updateRaw(innerList) }

    override fun remove(element: E): Boolean = innerList.remove(element).also { updateRaw(innerList) }

    override fun add(index: Int, element: E) = innerList.add(index, element).also { updateRaw(innerList) }

    override fun addAll(index: Int, elements: Collection<E>): Boolean = innerList.addAll(index, elements).also { updateRaw(innerList) }

    override fun clear() = innerList.clear().also { runCatching {
        clearRaw()
    } }

    override fun removeAll(elements: Collection<E>): Boolean = innerList.removeAll(elements).also { updateRaw(innerList) }

    override fun removeAt(index: Int): E = innerList.removeAt(index).also { updateRaw(innerList) }

    override fun retainAll(elements: Collection<E>): Boolean = innerList.retainAll(elements).also { updateRaw(innerList) }

    override fun set(index: Int, element: E): E = innerList.set(index, element).also { updateRaw(innerList) }

    override val size: Int
        get() = innerList.size

    override fun contains(element: E): Boolean = innerList.contains(element)

    override fun containsAll(elements: Collection<E>): Boolean = innerList.containsAll(elements)

    override fun get(index: Int): E = innerList.get(index)

    override fun indexOf(element: E): Int = innerList.indexOf(element)

    override fun isEmpty(): Boolean = innerList.isEmpty()

    override fun iterator(): MutableIterator<E> = innerList.iterator()

    override fun lastIndexOf(element: E): Int = innerList.lastIndexOf(element)

    override fun listIterator(): MutableListIterator<E> = innerList.listIterator()

    override fun listIterator(index: Int): MutableListIterator<E> = innerList.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> = innerList.subList(fromIndex, toIndex)
}
