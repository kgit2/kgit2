pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }

    val kotlinVersion: String by settings
    val kspVersion: String by settings
    plugins {
        kotlin("jvm") version kotlinVersion apply false
        kotlin("multiplatform") version kotlinVersion apply false
        id("org.jetbrains.dokka") version kotlinVersion apply false
        id("com.google.devtools.ksp") version kspVersion apply false
    }
}

buildCache {
    local {
        directory = File(rootDir, "build-cache")
        removeUnusedEntriesAfterDays = 30
    }
}

rootProject.name = "kgit2"

// include(":core")
include(":annotations")
include(":ksp")
// include(":lib")
// include(":native")

// val bitmask = File(settingsDir,"../bitmask")
// if (bitmask.exists()) {
//     includeBuild(bitmask)
// }

// val kommand = File(settingsDir, "../kommand")
// if (kommand.exists()) {
//     includeBuild(kommand)
// }
