package com.kgit2.callback.payload

import com.kgit2.common.option.mutually.PackBuilderStage

interface PackProgress {
    fun packProgress(
        stage: PackBuilderStage,
        current: Int,
        total: Int,
    )
}
