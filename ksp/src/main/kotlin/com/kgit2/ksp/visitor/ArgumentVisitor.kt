package com.kgit2.ksp.visitor

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.visitor.KSDefaultVisitor
import com.kgit2.ksp.ProcessorBase
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Single

@Single
@ComponentScan
class ArgumentVisitor(
    override val environment: SymbolProcessorEnvironment
) : KSDefaultVisitor<Unit, Sequence<KSType>>(), ProcessorBase {
    override fun defaultHandler(node: KSNode, data: Unit): Sequence<KSType> {
        return (((node as KSValueArgument).value as KSType).declaration as KSClassDeclaration).superTypes.map { it.resolve() }
    }
}
