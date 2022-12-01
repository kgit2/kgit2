package com.kgit2.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
annotation class Raw(
    val base: KClass<*>,
    val free: String = "",
    val pointerFree: String = "",
    val secondaryFree: String = "",
    val beforeFree: String = "",
    val shouldFreeOnFailure: Boolean = false,
)
