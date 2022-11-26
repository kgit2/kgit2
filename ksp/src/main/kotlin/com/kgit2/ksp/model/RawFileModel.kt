package com.kgit2.ksp.model

data class RawFileModel(
    val fileName: String,
    val packagePath: String,
    val modules: MutableSet<RawDeclareModel> = mutableSetOf()
) {
    var packageName: String = if (packagePath.endsWith("object")) {
        packagePath.substringBeforeLast(".object") + """.`object`"""
    } else {
        packagePath
    }
}
