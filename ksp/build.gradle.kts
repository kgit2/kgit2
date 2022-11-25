val kotlinVersion: String by rootProject
val kspVersion: String by rootProject
val koinVersion: String by rootProject
val koinKspVersion: String by rootProject

plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

dependencies {
    implementation(project(":annotations"))
    implementation("org.freemarker:freemarker:2.3.31")
    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("io.insert-koin:koin-annotations:$koinKspVersion")
    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    ksp("io.insert-koin:koin-ksp-compiler:$koinKspVersion")
}

sourceSets {
    val main by getting {
        kotlin.srcDirs("build/generated/ksp/main")
    }
}
