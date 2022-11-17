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

include(":core")

val bitmask = File(settingsDir,"../bitmask")
if (bitmask.exists()) {
    includeBuild(bitmask)
}

val kommand = File(settingsDir, "../kommand")
if (kommand.exists()) {
    includeBuild(kommand)
}
