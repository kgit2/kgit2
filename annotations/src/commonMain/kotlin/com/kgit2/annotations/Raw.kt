package com.kgit2.annotations

import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

@Target(AnnotationTarget.CLASS)
annotation class Raw(
    val base: KClass<*>,
    val free: String = "",
    val shouldFreeOnFailure: Boolean = false,
)
