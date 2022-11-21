val kotlinVersion: String by rootProject
val kotlinXCoroutinesVersion: String by rootProject
val kspVersion: String by rootProject
val bitmaskVersion: String by rootProject
val kommandVersion: String by rootProject

plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
}

allprojects {
    group = "com.kgit2"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenLocal()
        mavenCentral()
    }
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
                implementation("com.kgit2:bitmask-library:$bitmaskVersion")
            }
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
                    defFile(rootProject.file("lib/libgit2.def"))
                    packageName("libgit2")
                }

                val libdemo by creating {
                    defFile(rootProject.file("lib/libdemo.def"))
                    packageName("libdemo")
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
    add("kspCommonMainMetadata", "com.kgit2:bitmask-processor:$kspVersion")
    add("kspNative", "com.kgit2:bitmask-processor:$kspVersion")
}

tasks {
    val generateDef by registering {
        group = "interop"
        outputs.cacheIf { true }
        outputs.file(rootProject.file("lib/libgit2.def"))
        doLast {
            val libDir = rootProject.file("lib").normalize()
            val libgit2Dir = File(libDir, "libgit2").normalize()
            val libssh2Dir = File(libDir, "libssh2").normalize()
            val pb = ProcessBuilder("sh", "-c", "pkg-config --libs libgit2 --static")
            pb.environment()["PKG_CONFIG_PATH"] = File(libgit2Dir, "lib/pkgconfig").normalize().absolutePath
            pb.redirectInput(ProcessBuilder.Redirect.PIPE)
            val child = pb.start()
            val pkgResult = child.inputStream.bufferedReader().readText()
            child.waitFor()
            val template = """
                |headers = git2.h
                |staticLibraries = libgit2.a
                |libraryPaths = ${File(libgit2Dir, "lib").normalize().absolutePath} ${File(libssh2Dir, "lib").normalize().absolutePath}
                |compilerOpts = -I${File(libgit2Dir, "include").normalize().absolutePath}
                |linkerOpts = $pkgResult
            """.trimMargin()
            rootProject.file("lib/libgit2.def").writeText(template)
        }
    }

    val cinteropLibgit2Native by getting {
        dependsOn(generateDef)
    }
}
