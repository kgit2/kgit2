package com.kgit2.repository

import com.kgit2.annotations.Raw
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.memory.GitBase
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
) : GitBase<git_repository_init_options, RepositoryInitOptionsRaw>(raw) {
    constructor(memory: Memory, handler: RepositoryInitOptionsPointer) : this(RepositoryInitOptionsRaw(memory, handler))

    var flags: UInt = RepositoryInitFlat.MkDir.value
        .and(RepositoryInitFlat.MkPath.value)
        .and(RepositoryInitFlat.ExternalTemplate.value)
        private set(value) {
            field = value
            raw.handler.pointed.flags = value
        }

    var mode: UInt = raw.handler.pointed.mode
        private set(value) {
            field = value
            raw.handler.pointed.mode = value
        }

    var workdir_path: String? = raw.handler.pointed.workdir_path?.toKString()
        private set(value) {
            field = value
            raw.handler.pointed.workdir_path = value?.cstr?.getPointer(raw.memory)
        }

    var description: String? = raw.handler.pointed.description?.toKString()
        private set(value) {
            field = value
            raw.handler.pointed.description = value?.cstr?.getPointer(raw.memory)
        }

    var template_path: String? = raw.handler.pointed.template_path?.toKString()
        private set(value) {
            field = value
            raw.handler.pointed.template_path = value?.cstr?.getPointer(raw.memory)
        }

    var initial_head: String? = raw.handler.pointed.initial_head?.toKString()
        private set(value) {
            field = value
            raw.handler.pointed.initial_head = value?.cstr?.getPointer(raw.memory)
        }

    var origin_url: String? = raw.handler.pointed.origin_url?.toKString()
        private set(value) {
            field = value
            raw.handler.pointed.origin_url = value?.cstr?.getPointer(raw.memory)
        }

    fun flag(
        flag: RepositoryInitFlat,
        on: Boolean,
    ): RepositoryInitOptions {
        when (on) {
            true -> this.flags = this.flags.or(flag.value)
            false -> this.flags = this.flags.and(flag.value.inv())
        }
        return this
    }

    fun bare(on: Boolean): RepositoryInitOptions {
        return flag(RepositoryInitFlat.Bare, on)
    }

    fun noReinit(on: Boolean): RepositoryInitOptions {
        return flag(RepositoryInitFlat.NoReInit, on)
    }

    fun noDotGitDir(on: Boolean): RepositoryInitOptions {
        return flag(RepositoryInitFlat.NoDotDir, on)
    }

    fun mkdir(on: Boolean): RepositoryInitOptions {
        return flag(RepositoryInitFlat.MkDir, on)
    }

    fun mkpath(on: Boolean): RepositoryInitOptions {
        return flag(RepositoryInitFlat.MkPath, on)
    }

    fun externalTemplate(on: Boolean): RepositoryInitOptions {
        return flag(RepositoryInitFlat.ExternalTemplate, on)
    }

    fun relativeGitLink(on: Boolean): RepositoryInitOptions {
        return flag(RepositoryInitFlat.RelativeLink, on)
    }

    fun mode(mode: RepositoryInitMode): RepositoryInitOptions {
        this.mode = mode.value
        return this
    }

    fun workdirPath(path: String): RepositoryInitOptions {
        this.workdir_path = path
        return this
    }

    fun description(description: String): RepositoryInitOptions {
        this.description = description
        return this
    }

    fun templatePath(path: String): RepositoryInitOptions {
        this.template_path = path
        return this
    }

    fun initialHead(head: String): RepositoryInitOptions {
        this.initial_head = head
        return this
    }

    fun originUrl(url: String): RepositoryInitOptions {
        this.origin_url = url
        return this
    }

    // fun toRaw(memory: Memory): CValuesRef<git_repository_init_options> {
    //     val opts = memory.alloc<git_repository_init_options>()
    //     assertEquals(git_repository_init_init_options(opts.ptr, GIT_REPOSITORY_INIT_OPTIONS_VERSION), 0)
    //     opts.flags = this@RepositoryInitOptions.flags
    //     opts.mode = this@RepositoryInitOptions.mode
    //     opts.workdir_path = this@RepositoryInitOptions.workdir_path?.cstr?.getPointer(memory)
    //     opts.description = this@RepositoryInitOptions.description?.cstr?.getPointer(memory)
    //     opts.template_path = this@RepositoryInitOptions.template_path?.cstr?.getPointer(memory)
    //     opts.initial_head = this@RepositoryInitOptions.initial_head?.cstr?.getPointer(memory)
    //     opts.origin_url = this@RepositoryInitOptions.origin_url?.cstr?.getPointer(memory)
    //     return opts.ptr
    // }
}
