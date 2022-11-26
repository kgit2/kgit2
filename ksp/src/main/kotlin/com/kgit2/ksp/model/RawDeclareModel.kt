package com.kgit2.ksp.model

import org.jetbrains.kotlin.com.google.common.base.CaseFormat

data class RawDeclareModel(
    val git2Name: String,
    val structVar: Boolean,
    val freeOnFailure: String?,
    val shouldFreeOnFailure: Boolean = false,
) {
    val moduleName: String = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, git2Name.replace("git_", ""))
}
