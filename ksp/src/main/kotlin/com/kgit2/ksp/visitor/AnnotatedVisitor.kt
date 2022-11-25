package com.kgit2.ksp.visitor

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.visitor.KSDefaultVisitor
import com.kgit2.annotations.InitialPointerType
import com.kgit2.ksp.ProcessorBase
import com.kgit2.ksp.model.ModuleDataModel
import com.kgit2.ksp.model.ModuleFileModel
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Factory

@Factory
@ComponentScan
class AnnotatedVisitor(
    override val environment: SymbolProcessorEnvironment,
) : KSDefaultVisitor<MutableMap<String, ModuleFileModel>, Unit>(), ProcessorBase {
    override fun defaultHandler(node: KSNode, data: MutableMap<String, ModuleFileModel>) {
        if (node !is KSDeclaration) return
        defaultHandler(node, data)
    }

    private fun defaultHandler(annotated: KSDeclaration, data: MutableMap<String, ModuleFileModel>) {
        val rawAnnotation = annotated.annotations.find { it.shortName.asString() == "Raw" }!!
        val file = annotated.parent
        if (file !is KSFile) return
        val fileModel = data[file.filePath] ?: ModuleFileModel(file.fileName, file.packageName.asString())
        val argumentMap = rawAnnotation.arguments.associateBy { it.name!!.asString() }
        fileModel.modules.add(
            ModuleDataModel(
                git2Name = argumentMap["base"]!!.value as String,
                secondaryPointer = (argumentMap["initialPointer"]!!.value as KSType).declaration.simpleName.asString() == InitialPointerType.SECONDARY.name,
                freeOnFailure = argumentMap["free"]?.value as String?,
                shouldFreeOnFailure = (argumentMap["shouldFreeOnFailure"]?.value as Boolean?) ?: false,
            )
        )
        data[file.filePath] = fileModel
    }
}
