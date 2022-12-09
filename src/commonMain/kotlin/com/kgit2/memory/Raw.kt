package com.kgit2.memory

import com.kgit2.common.memory.Memory
// import io.github.aakira.napier.Napier
import kotlinx.atomicfu.AtomicBoolean
import kotlinx.atomicfu.atomic
import kotlinx.cinterop.CPointed
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CValues
import kotlinx.cinterop.CVariable

typealias BeforeFree = () -> Unit

abstract class Raw<T : CPointed>(
    val memory: Memory,
    val handler: CPointer<T>,
) : FreeAble {
    protected val freed: AtomicBoolean = atomic(false)

    open var beforeFree: BeforeFree? = null

    override fun free() {
        if (freed.compareAndSet(expect = false, update = true)) {
            // Napier.v("Freeing ${this::class.simpleName} with handler $handler")
            runCatching {
                beforeFree?.invoke()
            }.onFailure {
                // Napier.e("Error while running beforeFree", it)
            }
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
