package com.kgit2.diff

import com.kgit2.annotations.Raw
import com.kgit2.memory.GitBase
import kotlinx.cinterop.pointed
import libgit2.GIT_DIFF_PATCHID_OPTIONS_VERSION
import libgit2.git_diff_patchid_options
import libgit2.git_diff_patchid_options_init

@Raw(
    base = git_diff_patchid_options::class
)
class DiffPatchIDOptions(
    raw: DiffPatchidOptionsRaw = DiffPatchidOptionsRaw(initial = {
        git_diff_patchid_options_init(this, GIT_DIFF_PATCHID_OPTIONS_VERSION)
    })
) : GitBase<git_diff_patchid_options, DiffPatchidOptionsRaw>(raw)
