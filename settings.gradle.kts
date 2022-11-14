pluginManagement {
    val kotlinVersion: String by settings
    val kspVersion: String by settings
    plugins {
        kotlin("jvm") version kotlinVersion apply false
        kotlin("multiplatform") version kotlinVersion apply false
        id("org.jetbrains.dokka") version kotlinVersion apply false
        id("com.google.devtools.ksp") version kspVersion apply false
    }

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "kgit2"

val bitmask = File(settingsDir,"../bitmask/library")
if (bitmask.exists()) {
    include(":bitmask-library")
    project(":bitmask-library").projectDir = bitmask
}

val processor = File(settingsDir, "../bitmask/processor")
if (processor.exists()) {
    include(":bitmask-processor")
    project(":bitmask-processor").projectDir = processor
}
