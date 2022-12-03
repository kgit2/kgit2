package com.kgit2.describe

import com.kgit2.annotations.Raw
import com.kgit2.common.extend.errorCheck
import com.kgit2.memory.RawWrapper
import com.kgit2.model.Buf
import com.kgit2.`object`.Object
import com.kgit2.repository.Repository
import kotlinx.cinterop.ptr
import libgit2.*

@Raw(
    base = git_describe_result::class,
    free = "git_describe_result_free"
)
class Describe(raw: DescribeResultRaw) : RawWrapper<git_describe_result, DescribeResultRaw>(raw) {
    constructor(secondaryInitial: DescribeResultSecondaryInitial) : this(DescribeResultRaw(secondaryInitial = secondaryInitial))

    constructor(target: Object, options: DescribeOptions) : this(secondaryInitial = {
        git_describe_commit(this.ptr, target.raw.handler, options.raw.handler).errorCheck()
    })

    constructor(repository: Repository, options: DescribeOptions) : this(secondaryInitial = {
        git_describe_workdir(this.ptr, repository.raw.handler, options.raw.handler).errorCheck()
    })

    fun format(options: DescribeFormatOptions): Buf = Buf {
        git_describe_format(this, raw.handler, options.raw.handler).errorCheck()
    }


}
