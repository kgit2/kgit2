package com.kgit2.ksp.visitor.raw

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.visitor.KSDefaultVisitor
import com.kgit2.annotations.Raw
import com.kgit2.ksp.ProcessorBase
import com.kgit2.ksp.model.RawDeclareModel
import com.kgit2.ksp.model.RawFileModel
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Factory

@Factory
@ComponentScan
class RawAnnotatedVisitor(
    override val environment: SymbolProcessorEnvironment,
    private val argumentVisitor: ArgumentVisitor,
) : KSDefaultVisitor<MutableMap<String, RawFileModel>, Unit>(), ProcessorBase {
    override fun defaultHandler(node: KSNode, data: MutableMap<String, RawFileModel>) {
        if (node !is KSDeclaration) return
        defaultHandler(node, data)
    }

    private fun defaultHandler(annotated: KSDeclaration, data: MutableMap<String, RawFileModel>) {
        val rawAnnotation = annotated.annotations.find { it.shortName.asString() == Raw::class::simpleName.get() }!!
        val file = annotated.parent
        if (file !is KSFile) return
        val fileModel = data[file.filePath] ?: RawFileModel(file.fileName, file.packageName.asString())
        val argumentMap = rawAnnotation.arguments.associateBy { it.name!!.asString() }
        val superTypeOfBase = argumentMap[Raw::base.name]!!.accept(argumentVisitor, Unit)
        val structVar = superTypeOfBase.any { it.declaration.simpleName.asString() == "CStructVar" }
        fileModel.modules.add(
            RawDeclareModel(
                git2Name = (argumentMap[Raw::base.name]!!.value as KSType).declaration.simpleName.asString(),
                structVar = structVar,
                freeOnFailure = argumentMap[Raw::free.name]?.value as String?,
                shouldFreeOnFailure = (argumentMap[Raw::shouldFreeOnFailure.name]?.value as Boolean?) ?: false,
            )
        )
        data[file.filePath] = fileModel
    }
}
