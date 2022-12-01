package com.kgit2.oid

import com.kgit2.annotations.Raw
import com.kgit2.blob.Blob
import com.kgit2.common.lifetime.Lifetime
import com.kgit2.common.memory.Memory
import com.kgit2.memory.RawWrapper
import kotlinx.cinterop.*
import libgit2.git_oidarray

@Raw(
    base = git_oidarray::class,
    free = "git_oidarray_free",
)
class OidArray(raw: OidarrayRaw) : RawWrapper<git_oidarray, OidarrayRaw>(raw) {
    constructor(
        memory: Memory = Memory(),
        handler: OidarrayPointer = memory.alloc<git_oidarray>().ptr,
        initial: OidarrayInitial? = null,
    ) : this(OidarrayRaw(memory, handler, initial))

    val size: Int = raw.handler.pointed.count.convert()

    /**
     * @return the [Oid] has a shorter lifetime than the [OidArray].
     * When OidArray is recycled by gc, Oid will also be unavailable.
     */
    @Lifetime("the [Oid] has a shorter lifetime than the [OidArray]")
    operator fun get(index: Int): Blob {
        if (index < 0 || index >= size) {
            throw NoSuchElementException("Index $index out of bounds for size $size")
        }
        return Oid(Memory(), raw.handler.pointed.ids!![index]) as Blob
    }
}
