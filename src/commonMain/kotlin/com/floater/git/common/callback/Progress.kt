package com.floater.git.common.callback

interface Progress {
    fun progress(path: String, completedSteps: Int, totalSteps: Int)
}
