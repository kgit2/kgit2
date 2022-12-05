package com.kgit2.repository

import com.kgit2.annotations.Raw
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.RawWrapper
import kotlinx.cinterop.cstr
import kotlinx.cinterop.pointed
import kotlinx.cinterop.toKString
import libgit2.GIT_REPOSITORY_INIT_OPTIONS_VERSION
import libgit2.git_repository_init_init_options
import libgit2.git_repository_init_options

@Raw(
    base = git_repository_init_options::class,
)
class RepositoryInitOptions(
    raw: RepositoryInitOptionsRaw = RepositoryInitOptionsRaw(initial = {
        git_repository_init_init_options(this, GIT_REPOSITORY_INIT_OPTIONS_VERSION).errorCheck()
    }),
    initial: RepositoryInitOptions.() -> Unit = {},
) : RawWrapper<git_repository_init_options, RepositoryInitOptionsRaw>(raw) {
    constructor(memory: Memory, handler: RepositoryInitOptionsPointer) : this(RepositoryInitOptionsRaw(memory, handler))

    val flags: RepositoryInitFlags = RepositoryInitFlags(raw.handler.pointed.flags) {
        raw.handler.pointed.flags = it
    }

    var mode: RepositoryInitMode = RepositoryInitMode.from(raw.handler.pointed.mode)
        set(value) {
            field = value
            raw.handler.pointed.mode = value.value
        }

    var workdirPath: String? = raw.handler.pointed.workdir_path?.toKString()
        set(value) {
            field = value
            raw.handler.pointed.workdir_path = value?.cstr?.getPointer(raw.memory)
        }

    var description: String? = raw.handler.pointed.description?.toKString()
        set(value) {
            field = value
            raw.handler.pointed.description = value?.cstr?.getPointer(raw.memory)
        }

    var templatePath: String? = raw.handler.pointed.template_path?.toKString()
        set(value) {
            field = value
            raw.handler.pointed.template_path = value?.cstr?.getPointer(raw.memory)
        }

    var initialHead: String? = raw.handler.pointed.initial_head?.toKString()
        set(value) {
            field = value
            raw.handler.pointed.initial_head = value?.cstr?.getPointer(raw.memory)
        }

    var originUrl: String? = raw.handler.pointed.origin_url?.toKString()
        set(value) {
            field = value
            raw.handler.pointed.origin_url = value?.cstr?.getPointer(raw.memory)
        }

    init {
        this.initial()
    }
}
