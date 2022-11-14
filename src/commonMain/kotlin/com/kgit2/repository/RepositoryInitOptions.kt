package com.kgit2.repository

import kotlinx.cinterop.*
import libgit2.GIT_REPOSITORY_INIT_OPTIONS_VERSION
import libgit2.git_repository_init_init_options
import libgit2.git_repository_init_options
import kotlin.test.assertEquals

data class RepositoryInitOptions(
    var flags: UInt = RepositoryInitFlat.MkDir.value
        .and(RepositoryInitFlat.MkPath.value)
        .and(RepositoryInitFlat.ExternalTemplate.value),
    var mode: UInt = 0u,
    var workdir_path: String? = null,
    var description: String? = null,
    var template_path: String? = null,
    var initial_head: String? = null,
    var origin_url: String? = null,
) {
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

    fun toRaw(arena: ArenaBase): CValuesRef<git_repository_init_options> {
        val opts = arena.alloc<git_repository_init_options>()
        assertEquals(git_repository_init_init_options(opts.ptr, GIT_REPOSITORY_INIT_OPTIONS_VERSION), 0)
        opts.flags = this@RepositoryInitOptions.flags
        opts.mode = this@RepositoryInitOptions.mode
        opts.workdir_path = this@RepositoryInitOptions.workdir_path?.cstr?.getPointer(arena)
        opts.description = this@RepositoryInitOptions.description?.cstr?.getPointer(arena)
        opts.template_path = this@RepositoryInitOptions.template_path?.cstr?.getPointer(arena)
        opts.initial_head = this@RepositoryInitOptions.initial_head?.cstr?.getPointer(arena)
        opts.origin_url = this@RepositoryInitOptions.origin_url?.cstr?.getPointer(arena)
        return opts.ptr
    }
}
