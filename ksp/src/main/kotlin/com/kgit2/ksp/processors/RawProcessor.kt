package com.kgit2.ksp.processors

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.kgit2.annotations.Raw
import com.kgit2.ksp.ProcessorBase
import com.kgit2.ksp.model.RawFileModel
import com.kgit2.ksp.visitor.raw.RawAnnotatedVisitor
import freemarker.template.Configuration
import koin
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Factory

@Factory
@ComponentScan
class RawProcessor(
    override val environment: SymbolProcessorEnvironment,
    val configuration: Configuration,
): SymbolProcessor, ProcessorBase {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(Raw::class.qualifiedName!!)
        logger.info("Found ${symbols.count()} symbols with @Raw")
        val visitor = koin.get<RawAnnotatedVisitor>()
        val fileModelMap = mutableMapOf<String, RawFileModel>()
        symbols.forEach {
            it.accept(visitor, fileModelMap)
        }
        for ((path, fileModel) in fileModelMap) {
            val writer = codeGenerator.createNewFile(
                dependencies = Dependencies(true),
                packageName = fileModel.packagePath,
                fileName = fileModel.fileName.split(".").first() + "Raw",
                extensionName = fileModel.fileName.split(".").last()
            ).writer()
            val template = configuration.getTemplate("raw-file.ftl")
            template.process(fileModel, writer)
        }
        return emptyList()
    }
}
