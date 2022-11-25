package com.kgit2.annotations

@Target(AnnotationTarget.CLASS)
annotation class Raw(
    val base: String,
    val initialPointer: InitialPointerType,
    val free: String = "",
    val shouldFreeOnFailure: Boolean = false,
)

enum class InitialPointerType {
    POINTER,
    SECONDARY,
}
