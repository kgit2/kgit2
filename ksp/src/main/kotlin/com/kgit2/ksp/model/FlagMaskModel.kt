package com.kgit2.ksp.model

import org.jetbrains.kotlin.com.google.common.base.CaseFormat

data class FlagMaskModel(
    val className: String,
    val flagsType: String,
    val flagsMutable: Boolean,
    val methods: Set<FlagMaskMethod>
)

data class FlagMaskMethod(
    val name: String,
    val value: String
) {
    val upperName: String = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, name)
}
