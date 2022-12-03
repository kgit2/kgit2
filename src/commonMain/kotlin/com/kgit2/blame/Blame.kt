package com.kgit2.blame

import cnames.structs.git_blame
import com.kgit2.annotations.Raw
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.IterableBase
import com.kgit2.memory.RawWrapper
import com.kgit2.repository.Repository
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import libgit2.git_blame_buffer
import libgit2.git_blame_file
import libgit2.git_blame_get_hunk_byindex
import libgit2.git_blame_get_hunk_count

@Raw(
    base = git_blame::class,
    free = "git_blame_free",
)
class Blame(raw: BlameRaw) : RawWrapper<git_blame, BlameRaw>(raw), IterableBase<BlameHunk> {
    constructor(secondaryInitial: BlameSecondaryInitial) : this(BlameRaw(secondaryInitial = secondaryInitial))

    constructor(repository: Repository, path: String, options: BlameOptions) : this(secondaryInitial = {
        git_blame_file(this.ptr, repository.raw.handler, path, options.raw.handler).errorCheck()
    })

    constructor(reference: Blame, buffer: ByteArray, memory: Memory = Memory()) : this(BlameRaw(memory = memory, secondaryInitial = {
        git_blame_buffer(this.ptr, reference.raw.handler, buffer.toKString(), buffer.size.toULong())
    }))

    override val size: Long = git_blame_get_hunk_count(raw.handler).toLong()

    override operator fun get(index: Long): BlameHunk =
        git_blame_get_hunk_byindex(raw.handler, index.toUInt())?.let { BlameHunk(it) }
            ?: throw IndexOutOfBoundsException()

    fun getByLine(lineNo: UInt): BlameHunk? = git_blame_get_hunk_byindex(raw.handler, lineNo)?.let { BlameHunk(it) }
}
