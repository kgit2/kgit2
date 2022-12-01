package com.kgit2.ksp.model

import org.jetbrains.kotlin.com.google.common.base.CaseFormat

data class RawDeclareModel(
    val git2Name: String,
    val structVar: Boolean,
    val free: String?,
    val pointerFree: String?,
    val secondaryFree: String?,
    val beforeFree: String?,
    val shouldFreeOnFailure: Boolean = false,
) {
    val moduleName: String = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, git2Name.replace("git_", ""))
}
