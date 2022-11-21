package com.kgit2.common.memory

import com.kgit2.memory.FreeAble
import kotlinx.atomicfu.atomic
import kotlinx.cinterop.*

open class Memory : AutofreeScope(), FreeAble {
    val arena = Arena()
    val isFreed = atomic(false)

    override fun alloc(size: Long, align: Int): NativePointed {
        return arena.alloc(size, align)
    }

    override fun free() {
        if (isFreed.compareAndSet(expect = false, update = true)) {
            arena.clear()
        }
    }

    val <T : CVariable> CValues<T>.ptr: CPointer<T>
        get() = this@ptr.getPointer(this@Memory.arena)
}

public inline fun <R> memoryScoped(block: Memory.()->R): R {
    val memory = Memory()
    try {
        return memory.block()
    } finally {
        memory.free()
    }
}
