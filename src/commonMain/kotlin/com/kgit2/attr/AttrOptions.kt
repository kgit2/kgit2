package com.kgit2.attr

import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.memory.RawWrapper
import com.kgit2.oid.Oid
import kotlinx.cinterop.cValue
import kotlinx.cinterop.pointed
import libgit2.*

@Raw(
    base = git_attr_options::class,
)
class AttrOptions(
    raw: AttrOptionsRaw = AttrOptionsRaw(Memory(), cValue()),
) : RawWrapper<git_attr_options, AttrOptionsRaw>(raw) {
    val flags: AttrCheckFlags = AttrCheckFlags(raw.handler.pointed.flags)

    var commitId: Oid? = raw.handler.pointed.commit_id?.let { Oid(handler = it) }
        set(value) {
            field = value
            raw.handler.pointed.commit_id = value?.raw?.handler
        }

    var attrCommitId: Oid = Oid(Memory(), raw.handler.pointed.attr_commit_id)
        set(value) {
            field.copyFrom(value)
        }
}
