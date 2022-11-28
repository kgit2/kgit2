package com.kgit2.ksp.visitor.flag_mask

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.visitor.KSDefaultVisitor
import com.kgit2.annotations.FlagMask
import com.kgit2.ksp.ProcessorBase
import com.kgit2.ksp.model.FlagMaskFileModel
import com.kgit2.ksp.model.FlagMaskMethod
import com.kgit2.ksp.model.FlagMaskModel
import org.jetbrains.kotlin.com.google.common.base.CaseFormat
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Single

@Single
@ComponentScan
class FlagMaskAnnotatedVisitor(
    override val environment: SymbolProcessorEnvironment,
    private val sourceFileVisitor: SourceFileVisitor,
) : KSDefaultVisitor<MutableMap<String, FlagMaskFileModel>, Unit>(), ProcessorBase {
    override fun defaultHandler(node: KSNode, data: MutableMap<String, FlagMaskFileModel>) {
        if (node !is KSDeclaration) return
        defaultHandler(node, data)
    }

    private fun defaultHandler(annotated: KSDeclaration, data: MutableMap<String, FlagMaskFileModel>) = runCatching {
        val flagMaskAnnotation =
            annotated.annotations.find { it.shortName.asString() == FlagMask::class::simpleName.get() }!!
        // val fileModel = annotated.accept(sourceFileVisitor, data)
        val file = annotated.parent as KSFile
        val fileModel = data[file.filePath] ?: FlagMaskFileModel(file.fileName, file.packageName.asString())
        val argumentMap = flagMaskAnnotation.arguments.associateBy { it.name!!.asString() }
        val flags = (argumentMap[FlagMask::flags.name]!!.value as ArrayList<*>)
        fileModel.modules.add(
            FlagMaskModel(
                className = (annotated as KSClassDeclaration).simpleName.asString(),
                flagsType = (argumentMap[FlagMask::flagsType.name]!!.value as KSType).declaration.simpleName.asString(),
                flagsMutable = (argumentMap[FlagMask::flagsMutable.name]?.value as Boolean?) ?: true,
                methods = flags
                    .map { it as String }
                    .map {
                        FlagMaskMethod(
                            name = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, it.replace("GIT_", "")),
                            value = it,
                        )
                    }.toSet(),
            )
        )
        data[file.filePath] = fileModel
    }.onFailure {
        logger.error("", annotated)
    }.getOrThrow()
}
