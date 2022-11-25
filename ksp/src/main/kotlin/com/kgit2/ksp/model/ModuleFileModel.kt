package com.kgit2.ksp.model

data class ModuleFileModel(
    val fileName: String,
    val packagePath: String,
    val modules: MutableSet<ModuleDataModel> = mutableSetOf()
) {
    var packageName: String = if (packagePath.endsWith("object")) {
        packagePath.substringBeforeLast(".object") + """.`object`"""
    } else {
        packagePath
    }
}
