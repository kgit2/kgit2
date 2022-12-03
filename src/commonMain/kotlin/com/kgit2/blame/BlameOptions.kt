package com.kgit2.blame

import com.kgit2.annotations.Raw
import com.kgit2.common.memory.Memory
import com.kgit2.memory.RawWrapper
import com.kgit2.oid.Oid
import kotlinx.cinterop.pointed
import libgit2.GIT_BLAME_OPTIONS_VERSION
import libgit2.git_blame_options
import libgit2.git_blame_options_init

@Raw(
    base = git_blame_options::class
)
class BlameOptions(
    raw: BlameOptionsRaw = BlameOptionsRaw(initial = {
        git_blame_options_init(this, GIT_BLAME_OPTIONS_VERSION)
    })
) : RawWrapper<git_blame_options, BlameOptionsRaw>(raw) {
    constructor(initial: BlameOptionsInitial) : this(BlameOptionsRaw(initial = initial))

    val flags: BlameFlag = BlameFlag(raw.handler.pointed.flags) {
        raw.handler.pointed.flags = it
    }

    var minMatchCharacters: UShort = raw.handler.pointed.min_match_characters
        set(value) {
            field = value
            raw.handler.pointed.min_match_characters = value
        }

    var newestCommit: Oid = Oid(Memory(), raw.handler.pointed.newest_commit)
        set(value) {
            field.copyFrom(value)
        }

    var oldestCommit: Oid = Oid(Memory(), raw.handler.pointed.oldest_commit)
        set(value) {
            field.copyFrom(value)
        }

    var minLine: ULong = raw.handler.pointed.min_line
        set(value) {
            field = value
            raw.handler.pointed.min_line = value
        }

    var maxLine: ULong = raw.handler.pointed.max_line
        set(value) {
            field = value
            raw.handler.pointed.max_line = value
        }
}
