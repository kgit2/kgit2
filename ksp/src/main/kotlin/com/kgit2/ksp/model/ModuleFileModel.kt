package com.kgit2.ksp.model

data class ModuleFileModel(
    val fileName: String,
    val packageName: String,
    val modules: MutableSet<ModuleDataModel> = mutableSetOf()
)
