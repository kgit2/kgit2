import java.io.ByteArrayOutputStream
import java.nio.file.Path

val kotlinVersion: String by rootProject
val kotlinXCoroutinesVersion: String by rootProject
val kspVersion: String by rootProject
val bitmaskVersion: String by rootProject
val kommandVersion: String by rootProject

plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosArm64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }


    sourceSets {
        all {
            languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            languageSettings.optIn("kotlin.ExperimentalStdlibApi")
        }

        val commonMain by getting {
            dependencies {
                implementation("com.squareup.okio:okio:3.2.0")
                implementation("io.ktor:ktor-client-core:2.1.2")
                implementation("io.github.aakira:napier:2.6.1")
                implementation("com.kgit2:kommand:$kommandVersion")
                implementation(project(":annotations"))
                // implementation("com.kgit2:bitmask-library:$bitmaskVersion")
            }
            kotlin.srcDirs("build/generated/ksp/metadata/commonMain")
            kotlin.srcDirs("build/generated/ksp/native/nativeMain/kotlin")
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinXCoroutinesVersion")
            }
        }
        val nativeMain by getting
        val nativeTest by getting
    }

    nativeTarget.apply {
        compilations.getByName("main") {
            cinterops {
                val libgit2 by creating {
                    defFile(rootProject.file("lib/build/cinterop/libgit2.def"))
                    packageName("libgit2")
                }

                val libnative by creating {
                    defFile(rootProject.file("lib/build/cinterop/libnative.def"))
                    packageName("libnative")
                }
            }
        }
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }
}

dependencies {
    // add("kspCommonMainMetadata", "com.kgit2:bitmask-processor:$kspVersion")
    // add("kspNative", "com.kgit2:bitmask-processor:$kspVersion")
    add("kspCommonMainMetadata", project(":ksp"))
    add("kspNative", project(":ksp"))
}

tasks {
    val wrapper by getting(Wrapper::class) {
        distributionType = Wrapper.DistributionType.ALL
        gradleVersion = "7.6"
    }

    val generateLeaksCheck by creating(Exec::class) {
        dependsOn("linkDebugTestNative")
        outputs.file(buildDir.resolve("bin/leaks/check_leaks"))
        commandLine("sh", "-c", "${buildDir.resolve("bin/native/debugTest/test.kexe")} --ktest_list_tests")
        val output = ByteArrayOutputStream()
        standardOutput = output
        doLast {
            val testList = String(output.toByteArray()).lines()
            val tests = mutableListOf<String>()
            testList.forEach {
                if (it.startsWith("com.kgit2.")) {
                    tests.add("$it*")
                }
            }
            var prefix = ""
            testList.forEach {
                if (it.startsWith("com.kgit2.")) {
                    prefix = it
                } else if (it.trim().isNotEmpty()) {
                    tests.add("$prefix${it.trim()}")
                }
            }

            val template = """
                |#!/usr/bin/env sh
                |
                |tests=(
                |${tests.joinToString("\n")}
                |)
                |
                |index=$1
                |
                |if [ ${"$"}index = "--list" ]; then
                |    for i in "${"$"}{!tests[@]}"; do
                |        echo "${"$"}i: ${"$"}{tests[${"$"}i]}"
                |    done
                |    exit 0
                |fi
                |
                |repeat=${"$"}{2:-1}
                |echo ${"$"}{tests[${"$"}{index}]}
                |leaks --atExit -- ${buildDir.resolve("bin/native/debugTest/test.kexe")} --ktest_filter=${"$"}{tests[${"$"}{index}]} --ktest_repeat=${"$"}repeat
            """.trimMargin()

            buildDir.resolve("bin/leaks/check_leaks").writeText(template)
            exec {
                commandLine("chmod", "+x", buildDir.resolve("bin/leaks/check_leaks").toString())
            }
        }
    }
}

allprojects {
    group = "com.kgit2"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenLocal()
        mavenCentral()
    }

    tasks.whenTaskAdded {
        if (name.startsWith("ksp")) {
            // logging.captureStandardError(LogLevel.ERROR)
            // logging.captureStandardOutput(LogLevel.DEBUG)
            group = "ksp"
            // TODO("should delete this line")
            // this.enabled = false
        }
    }
}
