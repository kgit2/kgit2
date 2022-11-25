package com.kgit2.ksp

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

interface ProcessorBase {
    val environment: SymbolProcessorEnvironment
    val options: Map<String, String>
        get() = environment.options
    val codeGenerator
        get() = environment.codeGenerator
    val logger
        get() = environment.logger
}
