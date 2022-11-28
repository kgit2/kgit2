package com.kgit2.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
annotation class FlagMask(
    val flagsType: KClass<*>,
    vararg val flags: String,
    val flagsMutable: Boolean = false,
)
