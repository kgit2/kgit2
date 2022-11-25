package com.kgit2.ksp.model

import org.jetbrains.kotlin.com.google.common.base.CaseFormat

data class ModuleDataModel(
    val git2Name: String,
    val secondaryPointer: Boolean,
    val freeOnFailure: String?,
    val shouldFreeOnFailure: Boolean = false,
) {
    val moduleName = CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, git2Name.replace("git_", ""))
}
