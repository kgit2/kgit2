plugins {
    kotlin("multiplatform") version "1.7.10"
}

group = "com.floater.git"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
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
        val commonMain by getting
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
