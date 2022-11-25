package com.kgit2.common.memory

import com.kgit2.memory.FreeAble
import kotlinx.atomicfu.atomic
import kotlinx.cinterop.*

open class Memory : AutofreeScope(), FreeAble {
    private val arenaInitialized = atomic(false)
    private val arena by lazy {
        arenaInitialized.compareAndSet(expect = false, update = true)
        Arena()
    }
    private val isFreed = atomic(false)

    override fun alloc(size: Long, align: Int): NativePointed {
        return arena.alloc(size, align)
    }

    override fun free() {
        if (isFreed.compareAndSet(expect = false, update = true)) {
            if (arenaInitialized.value) {
                arena.clear()
            }
        }
    }

    val <T : CVariable> CValues<T>.ptr: CPointer<T>
        get() = this@ptr.getPointer(this@Memory.arena)
}

inline fun <R> memoryScoped(block: Memory.() -> R): R {
    val memory = Memory()
    try {
        return memory.block()
    } finally {
        memory.free()
    }
}
