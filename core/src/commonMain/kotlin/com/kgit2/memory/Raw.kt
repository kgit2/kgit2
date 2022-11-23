package com.kgit2.memory

import com.kgit2.common.memory.Memory
import kotlinx.atomicfu.AtomicBoolean
import kotlinx.atomicfu.atomic
import kotlinx.cinterop.CPointed
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CValues
import kotlinx.cinterop.CVariable

typealias BeforeFree = () -> Unit

abstract class Raw<T: CPointed> (
    val memory: Memory,
    val handler: CPointer<T>,
) : FreeAble {
    protected val freed: AtomicBoolean = atomic(false)

    open val beforeFree: BeforeFree? = null

    override fun free() {
        if (freed.compareAndSet(expect = false, update = true)) {
            beforeFree?.invoke()
            memory.free()
        }
    }

    fun move() {
        freed.compareAndSet(expect = false, update = true)
    }

    fun isFreed(): Boolean = freed.value

    val <T : CVariable> CValues<T>.ptr: CPointer<T>
        get() = this@ptr.getPointer(this@Raw.memory)
}
