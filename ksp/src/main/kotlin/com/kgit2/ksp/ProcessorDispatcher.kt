package com.kgit2.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.kgit2.ksp.processors.FlagMaskProcessor
import com.kgit2.ksp.processors.RawProcessor
import koin
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Single

@Single
@ComponentScan
class ProcessorDispatcher(
    override val environment: SymbolProcessorEnvironment,
) : SymbolProcessor, ProcessorBase {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        koin.get<RawProcessor>().process(resolver)
        koin.get<FlagMaskProcessor>().process(resolver)
        return emptyList()
    }
}
