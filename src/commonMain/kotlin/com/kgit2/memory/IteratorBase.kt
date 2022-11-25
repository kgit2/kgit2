package com.kgit2.memory

import com.kgit2.exception.GitErrorCode
import com.kgit2.exception.GitError
import kotlinx.cinterop.CPointed

abstract class IteratorBase<T: CPointed, R: Raw<T>, E>(raw: R) : GitBase<T, R>(raw), Iterator<E> {
    var next: E? = null

    abstract fun nextRaw(): Result<E>

    override fun hasNext(): Boolean {
        nextRaw().onSuccess {
            this.next = it
        }.onFailure {
            if (it !is GitError || it.code != GitErrorCode.IterOver) {
                throw it
            }
            next = null
            return false
        }
        return true
    }

    override fun next(): E = next ?: throw NoSuchElementException()

    val list by lazy { asSequence().toList() }
}