package com.kgit2.model

import kotlinx.cinterop.*

interface AutoFree {
    fun free()
}

interface GitBase<T> {
    val handler: T
}

interface AutoFreeGitBase<T> : GitBase<T>, AutoFree {
    val arena: Arena

    val <T : CVariable> CValues<T>.ptr: CPointer<T>
        get() = this@ptr.getPointer(this@AutoFreeGitBase.arena)

    override fun free() {
        arena.clear()
    }
}

inline fun <T, R> AutoFreeGitBase<T>.autoFreeScoped(block: Arena.() -> R): R {
    try {
        return arena.block()
    } finally {
        free()
    }
}

