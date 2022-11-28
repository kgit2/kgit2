package com.kgit2.ksp.model

data class FlagMaskFileModel(
    val fileName: String,
    val packagePath: String,
    val imports: MutableSet<String> = mutableSetOf(),
    val modules: MutableSet<FlagMaskModel> = mutableSetOf()
) {
    var packageName: String = if (packagePath.endsWith("object")) {
        packagePath.substringBeforeLast(".object") + """.`object`"""
    } else {
        packagePath
    }
}
