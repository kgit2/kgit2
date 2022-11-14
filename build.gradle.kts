val bitmask = project.file("../bitmask/library")
val processor = project.file("../bitmask/processor")

val kotlinVersion: String by project
val kspVersion: String by project
val bitmaskVersion: String by project
val kommandVersion: String by project

plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
}

allprojects {
    group = "com.kgit2"
    version = "1.0-SNAPSHOT"

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
        val commonMain by getting {
            dependencies {
                implementation("com.squareup.okio:okio:3.2.0")
                implementation("io.ktor:ktor-client-core:2.1.2")
                implementation("io.github.aakira:napier:2.6.1")
                implementation("com.kgit2:kommand:$kommandVersion")
                if (bitmask.exists()) {
                    implementation(project(":bitmask-library"))
                } else {
                    implementation("com.kgit2:bitmask-library:$bitmaskVersion")
                }
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val nativeMain by getting
        val nativeTest by getting
    }

    nativeTarget.apply {
        compilations.getByName("main") {
            cinterops {
                val libgit2 by creating {
                    defFile(project.file("lib/libgit2.def"))
                    packageName("libgit2")
                }

                val libdemo by creating {
                    defFile(project.file("lib/libdemo.def"))
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

// dependencies {
//     add("kspCommonMainMetadata", if (processor.exists()) project(":bitmask-processor") else "com.kgit2:bitmask-processor:$kspVersion")
//     add("kspNative", if (processor.exists()) project(":bitmask-processor") else "com.kgit2:bitmask-processor:$kspVersion")
// }

tasks {
    val generateDef by registering {
        group = "interop"
        outputs.cacheIf { true }
        outputs.file(project.file("lib/libgit2.def"))
        doLast {
            val libDir = project.file("lib").normalize()
            val libgit2Dir = File(libDir, "libgit2").normalize()
            val libssh2Dir = File(libDir, "libssh2").normalize()
            val pb = ProcessBuilder("sh", "-c", "pkg-config --libs libgit2 --static")
            pb.environment()["PKG_CONFIG_PATH"] = "/Users/bppleman/floater/kgit2/lib/libgit2/lib/pkgconfig"
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
            project.file("lib/libgit2.def").writeText(template)
        }
    }

    val cinteropLibgit2Native by getting {
        dependsOn(generateDef)
    }
}
