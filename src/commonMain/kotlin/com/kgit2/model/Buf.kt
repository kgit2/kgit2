package com.kgit2.model

import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.memory.RawWrapper
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.cValue
import kotlinx.cinterop.convert
import kotlinx.cinterop.cstr
import kotlinx.cinterop.pointed
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.toCValues
import kotlinx.cinterop.usePinned
import libgit2.git_buf

@Raw(
    base = git_buf::class,
    free = "git_buf_dispose",
)
class Buf(
    raw: BufRaw = BufRaw(Memory(), cValue())
) : RawWrapper<git_buf, BufRaw>(raw) {
    constructor(
        content: String? = null,
        memory: Memory = Memory(),
    ) : this(BufRaw(memory, cValue<git_buf>().getPointer(memory)) {
        if (content != null) {
            this.pointed.ptr = content.cstr.getPointer(memory)
            this.pointed.size = content.length.convert()
        }
    })

    constructor(
        buffer: ByteArray,
        memory: Memory = Memory(),
    ) : this(BufRaw(memory, cValue<git_buf>().getPointer(memory)) {
        buffer.usePinned {
            this.pointed.ptr = it.addressOf(0)
        }
        this.pointed.size = buffer.size.convert()
    })

    constructor(
        memory: Memory = Memory(),
        handler: CPointer<git_buf> = cValue<git_buf>().getPointer(memory),
        initial: BufInitial? = null
    ) : this(BufRaw(memory, handler, initial))

    var buffer: ByteArray? = raw.handler.pointed.ptr?.readBytes(raw.handler.pointed.size.convert())
        set(value) {
            field = value
            raw.handler.pointed.ptr = value?.toCValues()?.getPointer(raw.memory)
            raw.handler.pointed.size = value?.size?.convert() ?: 0UL
        }
}
