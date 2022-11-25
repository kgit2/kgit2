package com.kgit2.annotations

@Target(AnnotationTarget.CLASS)
annotation class Raw(
    val base: String,
    val secondaryPointer: Boolean = true,
    val free: String = "",
    val shouldFreeOnFailure: Boolean = false,
)
