package com.kgit2.ksp.processors

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.kgit2.annotations.FlagMask
import com.kgit2.ksp.ProcessorBase
import com.kgit2.ksp.model.FlagMaskFileModel
import com.kgit2.ksp.visitor.flag_mask.FlagMaskAnnotatedVisitor
import freemarker.template.Configuration
import koin
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Factory

@Factory
@ComponentScan
class FlagMaskProcessor(
    override val environment: SymbolProcessorEnvironment,
    val configuration: Configuration,
): SymbolProcessor, ProcessorBase {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(FlagMask::class.qualifiedName!!)
        logger.warn("Found ${symbols.count()} symbols with @FlagMask")
        val visitor = koin.get<FlagMaskAnnotatedVisitor>()
        val fileModelMap = mutableMapOf<String, FlagMaskFileModel>()
        symbols.forEach {
            it.accept(visitor, fileModelMap)
        }
        for ((path, fileModel) in fileModelMap) {
            val writer = codeGenerator.createNewFile(
                dependencies = Dependencies(true),
                packageName = fileModel.packagePath,
                fileName = fileModel.fileName.split(".").first() + "Mask",
                extensionName = fileModel.fileName.split(".").last()
            ).writer()
            val template = configuration.getTemplate("flag-mask-file.ftl")
            template.process(fileModel, writer)
        }
        return emptyList()
    }
}
