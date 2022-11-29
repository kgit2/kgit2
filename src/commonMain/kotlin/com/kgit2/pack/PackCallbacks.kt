package com.kgit2.pack

import com.kgit2.common.error.GitErrorCode
import com.kgit2.common.option.mutually.PackBuilderStage

/**
 * Callback for the user's custom packbuilder progress.
 *
 * @param stage The current stage of the packbuilder
 * @param current The current value of the stage
 * @param total The total value of the stage
 * @return 0 to proceed with the packbuilder, < 0 to fail the packbuilder
 */
typealias PackBuilderProgressCallback = (stage: PackBuilderStage, current: ULong, total: ULong) -> GitErrorCode

interface PackProgress {
    fun packProgress(
        stage: PackBuilderStage,
        current: Int,
        total: Int,
    )
}
