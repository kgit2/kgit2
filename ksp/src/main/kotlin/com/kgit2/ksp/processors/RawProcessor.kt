package com.kgit2.ksp.processors

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.kgit2.annotations.Raw
import com.kgit2.ksp.ProcessorBase
import com.kgit2.ksp.model.ModuleFileModel
import com.kgit2.ksp.visitor.AnnotatedVisitor
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
        logger.warn("Found ${symbols.count()} symbols with @Raw")
        val visitor = koin.get<AnnotatedVisitor>()
        val fileModelMap = mutableMapOf<String, ModuleFileModel>()
        symbols.forEach {
            it.accept(visitor, fileModelMap)
        }
        for ((path, fileModel) in fileModelMap) {
            val writer = codeGenerator.createNewFile(
                Dependencies(true),
                fileModel.packagePath,
                fileModel.fileName.split(".").first() + "Raw",
                fileModel.fileName.split(".").last()
            ).writer()
            val template = configuration.getTemplate("module.ftl")
            template.process(fileModel, writer)
        }
        return emptyList()
    }
}
