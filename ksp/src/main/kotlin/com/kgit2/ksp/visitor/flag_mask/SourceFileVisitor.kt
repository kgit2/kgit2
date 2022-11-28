package com.kgit2.ksp.visitor.flag_mask

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.visitor.KSDefaultVisitor
import com.kgit2.ksp.ProcessorBase
import com.kgit2.ksp.model.FlagMaskFileModel
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Single
import java.io.File

@Single
@ComponentScan
class SourceFileVisitor(
    override val environment: SymbolProcessorEnvironment
) : KSDefaultVisitor<
    MutableMap<String, FlagMaskFileModel>,
    FlagMaskFileModel
    >(), ProcessorBase {
    override fun defaultHandler(node: KSNode, data: MutableMap<String, FlagMaskFileModel>): FlagMaskFileModel {
        return defaultHandler(node as KSClassDeclaration, data)
    }

    private fun defaultHandler(classNode: KSClassDeclaration, data: MutableMap<String, FlagMaskFileModel>): FlagMaskFileModel {
        val file = classNode.parent as KSFile
        val fileModel = data[file.filePath] ?: FlagMaskFileModel(file.fileName, file.packageName.asString())
        val flags = classNode.primaryConstructor!!.parameters.first()
        val flagsType = flags.type.toString()
        val imports = File(file.filePath).readLines().filter { it.matches(Regex("import .*")) }
        fileModel.imports.addAll(imports.filter { it.endsWith(flagsType) })
        data[file.filePath] = fileModel
        return fileModel
    }
}
