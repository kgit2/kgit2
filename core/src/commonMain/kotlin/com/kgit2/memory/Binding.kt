package com.kgit2.memory

import com.kgit2.common.memory.Memory
import kotlinx.atomicfu.AtomicBoolean
import kotlinx.atomicfu.atomic
import kotlinx.cinterop.CPointed
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CValues
import kotlinx.cinterop.CVariable

abstract class Binding<T: CPointed> (
    val memory: Memory,
    val handler: CPointer<T>,
) : FreeAble {
    val freed: AtomicBoolean = atomic(false)

    open val beforeFree: (() -> Unit)? = null

    override fun free() {
        if (freed.compareAndSet(expect = false, update = true)) {
            beforeFree?.invoke()
            memory.free()
        }
    }

    val <T : CVariable> CValues<T>.ptr: CPointer<T>
        get() = this@ptr.getPointer(this@Binding.memory)
}
