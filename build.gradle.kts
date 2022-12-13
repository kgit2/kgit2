import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentArchitecture
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentOperatingSystem
import java.io.InputStreamReader

val kotlinVersion: String by rootProject
val kotlinXCoroutinesVersion: String by rootProject
val kspVersion: String by rootProject
val bitmaskVersion: String by rootProject
val kommandVersion: String by rootProject
val ktorVersion: String by rootProject

enum class Platform {
    MACOS_X64,
    MACOS_ARM64,
    LINUX_X64,
    MINGW_X64,
    ;

    override fun toString(): String {
        return when (this) {
            MACOS_X64 -> "macosX64"
            MACOS_ARM64 -> "macosArm64"
            LINUX_X64 -> "linuxX64"
            MINGW_X64 -> "mingwX64"
        }
    }
}

val currentPlatform: Platform = when {
    getCurrentOperatingSystem().isMacOsX && getCurrentArchitecture().isAmd64 -> Platform.MACOS_X64
    getCurrentOperatingSystem().isMacOsX && getCurrentArchitecture().isArm -> Platform.MACOS_ARM64
    getCurrentOperatingSystem().isLinux && getCurrentArchitecture().isAmd64 -> Platform.LINUX_X64
    getCurrentOperatingSystem().isWindows && getCurrentArchitecture().isAmd64 -> Platform.MINGW_X64
    else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
}

plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val (nativeTarget, nativeTargetString) = when (currentPlatform) {
        Platform.MACOS_ARM64 -> macosArm64("native") to "macosArm64"
        Platform.MACOS_X64 -> macosX64("native") to "macosX64"
        Platform.LINUX_X64 -> linuxX64("native") to "linuxX64"
        Platform.MINGW_X64 -> mingwX64("native") to "mingwX64"
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
                // implementation("io.github.aakira:napier:2.6.1")
                implementation("com.kgit2:kommand:$kommandVersion") {
                    exclude("io.ktor", "ktor-client-core")
                }
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
                implementation("io.ktor:ktor-server-core:$ktorVersion")
            }
        }
        val nativeMain by getting
        val nativeTest by getting
    }

    nativeTarget.apply {
        compilations.getByName("main") {
            cinterops {
                val libgit2 by creating {
                    defFile(rootProject.file("src/nativeInterop/cinterop/${currentPlatform}.def"))
                    packageName(this@creating.name)
                }
            }
        }
        binaries {
            // executable {
            //     entryPoint = "main"
            // }
            staticLib {
                baseName = "kgit2"
            }
        }
        binaries.all {
            freeCompilerArgs += "-Xadd-light-debug=enable"
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", project(":ksp"))
    add("kspNative", project(":ksp"))
}

tasks {
    val wrapper by getting(Wrapper::class) {
        distributionType = Wrapper.DistributionType.ALL
        gradleVersion = "7.6"
    }

    val generateDef by creating {
        doLast {
            val staticLibraries = mutableListOf<String>()
            val libraryPaths = mutableListOf<File>()
            val compilerOpts = mutableListOf<File>()
            val linkerOpts = mutableListOf<String>()

            val libgit2Path = rootDir.resolve("libs").resolve(currentPlatform.toString()).resolve("libgit2")
            val libssh2Path = rootDir.resolve("libs").resolve(currentPlatform.toString()).resolve("libssh2")

            lateinit var defFile: File

            when (currentPlatform) {
                Platform.MACOS_ARM64 -> {
                    defFile = rootDir.resolve("src")
                        .resolve("nativeInterop")
                        .resolve("cinterop")
                        .resolve("${currentPlatform}.def")
                    val libsslPath = File(getBrewPrefix("openssl@3"))
                    staticLibraries.addAll(listOf("libssl.a", "libcrypto.a"))
                    libraryPaths.addAll(
                        listOf(
                            libgit2Path.resolve("lib"),
                            libssh2Path.resolve("lib"),
                            libsslPath.resolve("lib"),
                        )
                    )
                    compilerOpts.add(libgit2Path.resolve("include"))
                    val pkgConfig = getPkgConfig(
                        listOf(
                            libgit2Path.resolve("lib").resolve("pkgconfig").absolutePath,
                            libssh2Path.resolve("lib").resolve("pkgconfig").absolutePath,
                            libsslPath.resolve("lib").resolve("pkgconfig").absolutePath,
                        ), "--libs --static libgit2 libssh2 libssl libcrypto"
                    )
                    linkerOpts.addAll(pkgConfig.split(" ").filter { it.trim().startsWith("-L") })
                    linkerOpts.addAll("-framework CoreFoundation -framework Security -liconv -lz".split(" "))
                }
                else -> {}
            }
            val staticLibrariesTemplate = "staticLibraries = libgit2.a libssh2.a ${staticLibraries.joinToString(" ")}"
            val libraryPathsTemplate = "libraryPaths = ${libraryPaths.joinToString(" ")}"
            val compilerOptsTemplate =
                "compilerOpts = ${compilerOpts.joinToString(" ") { "-I${it.normalize().absolutePath}" }}"
            val linkerOptsTemplate = "linkerOpts = ${linkerOpts.joinToString(" ")}"
            val template = """
                |headers = git2.h git2/reflog.h git2/sys/reflog.h git2/sys/odb_backend.h git2/sys/mempack.h git2/sys/repository.h
                |$staticLibrariesTemplate
                |$libraryPathsTemplate
                |$compilerOptsTemplate
                |$linkerOptsTemplate
                |
                |noStringConversion = git_attr_value git_mailmap_from_buffer
                |
                |---
                |
                |const char *git_attr__true  = "[internal]__TRUE__";
                |const char *git_attr__false = "[internal]__FALSE__";
                |const char *git_attr__unset = "[internal]__UNSET__";
                |
            """.trimMargin()
            println(template)
            if (!defFile.exists()) {
                defFile.createNewFile()
            }
            println(defFile.absolutePath)
            println(defFile.isFile)
            defFile.writeText(template)
            val template1 = """
                |headers = git2.h git2/reflog.h git2/sys/reflog.h git2/sys/odb_backend.h git2/sys/mempack.h git2/sys/repository.h
                |staticLibraries = libgit2.a libssh2.a libssl.a libcrypto.a libz.a
                |libraryPaths = /Users/bppleman/kgit2/kgit2/libs/macosArm64/usr/lib
                |compilerOpts = -I/Users/bppleman/kgit2/kgit2/libs/macosArm64/usr/include
                |linkerOpts = -L/Users/bppleman/kgit2/kgit2/libs/macosArm64/usr/lib -L/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX12.3.sdk/usr/lib -framework CoreFoundation -framework Security -liconv
                |
                |noStringConversion = git_attr_value git_mailmap_from_buffer
                |
                |---
                |
                |const char *git_attr__true  = "[internal]__TRUE__";
                |const char *git_attr__false = "[internal]__FALSE__";
                |const char *git_attr__unset = "[internal]__UNSET__";
                |
            """.trimMargin()
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

fun getBrewPrefix(formula: String): String {
    if (!getCurrentOperatingSystem().isMacOsX) return ""
    return ProcessBuilder("sh", "-c", "brew --prefix $formula")
        .redirectInput(ProcessBuilder.Redirect.PIPE)
        .start()
        .inputStream
        .buffered()
        .let {
            InputStreamReader(it).readText().trim()
        }
}

fun getPkgConfig(pkgConfigPath: List<String>, args: String): String {
    // TODO: if windows use cmd \c
    return ProcessBuilder("sh", "-c", "pkg-config $args")
        .apply {
            environment()["PKG_CONFIG_PATH"] = pkgConfigPath.joinToString(":") { it.trim() }
        }
        .redirectInput(ProcessBuilder.Redirect.PIPE)
        .start()
        .inputStream
        .buffered()
        .let {
            InputStreamReader(it).readText().trim()
        }
}
