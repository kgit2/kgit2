import org.apache.tools.ant.taskdefs.condition.Os
import org.jetbrains.kotlin.de.undercouch.gradle.tasks.download.Download
import java.io.FileOutputStream

plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

val isMac = Os.isFamily(Os.FAMILY_MAC)
val isArm64 = Os.isArch("aarch64")

val libssh2Version = "1.10.0"
val libgit2Version = "1.5.0"

val libssh2Archive = buildDir.resolve("temp/libssh2-${libssh2Version}.tar.gz")
val libgit2Archive = buildDir.resolve("temp/libgit2-${libgit2Version}.tar.gz")

val libssh2SourceDir = buildDir.resolve("sources/libssh2")
val libgit2SourceDir = buildDir.resolve("sources/libgit2")

val libssh2BuildDir = libssh2SourceDir.resolve("build")
val libgit2BuildDir = libgit2SourceDir.resolve("build")

val opensslDir = if (isMac) {
    File(
        ProcessBuilder("sh", "-c", "brew --cellar openssl").redirectInput(ProcessBuilder.Redirect.PIPE)
            .start().inputStream.bufferedReader().readText()
    ).parentFile.parentFile.resolve("opt/openssl@3")
} else null
val libssh2DistDir = buildDir.resolve("dist/libssh2")
val libgit2DistDir = buildDir.resolve("dist/libgit2")

val cinterop = buildDir.resolve("cinterop")
val linkerOpts = cinterop.resolve("linker-opts.txt")
val defFile = cinterop.resolve("libgit2.def")

val libnativeSourceDir = projectDir.resolve("../native").normalize()
val libnativeCMake = buildDir.resolve("cmake/libnative")
val libnativeDist = buildDir.resolve("dist/libnative")
val libnativeDef = cinterop.resolve("libnative.def")

tasks {
    val wrapper by getting(Wrapper::class) {
        distributionType = Wrapper.DistributionType.ALL
        gradleVersion = "7.6"
    }

    val downloadLibssh2 by creating(Download::class) {
        group = "libssh2"
        outputs.file(libssh2Archive)
        src("https://github.com/libssh2/libssh2/releases/download/libssh2-${libssh2Version}/libssh2-${libssh2Version}.tar.gz")
        dest(libssh2Archive)
        overwrite(false)
    }

    val decompressLibssh2 by creating(Exec::class) {
        group = "libssh2"
        dependsOn(downloadLibssh2)
        inputs.file(downloadLibssh2.dest)
        outputs.dir(libssh2SourceDir)
        workingDir(buildDir)
        commandLine("tar", "-xzf", libssh2Archive, "-C", libssh2SourceDir, "--strip-components=1")
    }

    val configureLibssh2 by creating(Exec::class) {
        group = "libssh2"
        dependsOn(decompressLibssh2)
        outputs.files(
            libssh2SourceDir.resolve("Makefile"),
            libssh2SourceDir.resolve("Makefile.in")
        )
        workingDir(libssh2SourceDir.normalize().absolutePath)
        val command = commandLine(
            "sh", "-c",
            "./configure -q --prefix=${libssh2DistDir} " +
                    "--disable-silent-rules " +
                    "--disable-examples-build " +
                    "--with-libz " +
                    "--with-crypto=openssl " +
                    if (opensslDir != null) "--with-libssl-prefix=${opensslDir}" else "",
        )
        doFirst {
            logger.warn("Configure LibSSH2: ${command.executable} ${command.args?.joinToString(" ")}")
        }
    }

    val buildLibssh2 by creating(Exec::class) {
        group = "libssh2"
        dependsOn(configureLibssh2)
        outputs.dirs(libssh2SourceDir.resolve("src"))
        workingDir(libssh2SourceDir.normalize().absolutePath)
        val command = commandLine("sh", "-c", "make", "-s", "-j6")
        doLast {
            logger.warn("Build LibSSH2: ${command.executable} ${command.args?.joinToString(" ")}")
        }
    }

    val installLibssh2 by creating(Exec::class) {
        group = "libssh2"
        dependsOn(buildLibssh2)
        outputs.dir(libssh2DistDir)
        workingDir(libssh2SourceDir.normalize().absolutePath)
        val command = commandLine("make", "install", "-s", "-C", libssh2SourceDir)
        doLast {
            logger.warn("Install LibSSH2: ${command.executable} ${command.args?.joinToString(" ")}")
        }
    }

    val downloadLibgit2 by creating(Download::class) {
        group = "libgit2"
        outputs.file(libgit2Archive)
        src("https://github.com/libgit2/libgit2/archive/refs/tags/v${libgit2Version}.tar.gz")
        dest(libgit2Archive)
        overwrite(false)
    }

    val decompressLibgit2 by creating(Exec::class) {
        group = "libgit2"
        dependsOn(downloadLibgit2)
        inputs.file(downloadLibgit2.dest)
        outputs.dir(libgit2SourceDir)
        workingDir(buildDir)
        commandLine("tar", "-xzf", libgit2Archive, "-C", libgit2SourceDir, "--strip-components=1")
    }

    val configureLibgit2 by creating(Exec::class) {
        group = "libgit2"
        dependsOn(decompressLibgit2, installLibssh2)
        outputs.dir(libgit2BuildDir.resolve("CMakeFiles"))
        outputs.files(
            libgit2BuildDir.resolve("CMakeCache.txt"),
            libgit2BuildDir.resolve("Makefile"),
        )
        workingDir(libgit2BuildDir)
        errorOutput = System.err
        val command = commandLine(
            "sh", "-c",
            "cmake $libgit2SourceDir " +
                    "-DCMAKE_BUILD_TYPE=Release " +
                    "-DCMAKE_INSTALL_PREFIX=$libgit2DistDir " +
                    (if (isMac && isArm64) "-DCMAKE_OSX_ARCHITECTURES='arm64' " else "-DCMAKE_OSX_ARCHITECTURES='x86_64' ") +
                    "-DBUILD_SHARED_LIBS=OFF " +
                    "-DUSE_SSH=ON " +
                    "-DBUILD_TESTS=OFF " +
                    "-DCMAKE_PREFIX_PATH='$libssh2DistDir;$opensslDir'",
        )
        doLast {
            logger.warn("Configure LibSSH2: ${command.executable} ${command.args?.joinToString(" ")}")
        }
    }

    val installLibgit2 by creating(Exec::class) {
        group = "libgit2"
        dependsOn(configureLibgit2)
        outputs.dir(libgit2DistDir)
        workingDir(libgit2BuildDir)
        val command = commandLine(
            "sh", "-c",
            "cmake --build . --target install -j6"
        )
        doLast {
            logger.warn("Build & Install LibSSH2: ${command.executable} ${command.args?.joinToString(" ")}")
        }
    }

    val pkgConfig by creating(Exec::class) {
        group = "interop"
        dependsOn(installLibgit2)
        outputs.file(linkerOpts)
        workingDir(libgit2DistDir)
        environment(
            "PKG_CONFIG_PATH", listOf(
                libgit2DistDir.resolve("lib/pkgconfig").normalize().absolutePath,
                libssh2DistDir.resolve("lib/pkgconfig").normalize().absolutePath,
                opensslDir
            ).filterNotNull().joinToString(":")
        )
        val command = commandLine("sh", "-c", "pkg-config --libs libgit2 --static")
        doFirst {
            standardOutput = FileOutputStream(linkerOpts)
        }
        doLast {
            logger.warn("Generate linker options: ${command.executable} ${command.args?.joinToString(" ")}")
        }
    }

    val generateLibgit2Def by creating {
        group = "interop"
        inputs.file(linkerOpts)
        outputs.file(defFile)
        dependsOn(pkgConfig)
        doLast {
            val headers = mutableListOf("git2.h")
            libgit2DistDir.resolve("include/git2").listFiles()?.forEach {
                if (it.extension == "h") {
                    headers.add("git2/${it.name}")
                }
            }
            libgit2DistDir.resolve("include/git2/sys").listFiles()?.forEach {
                if (it.extension == "h") {
                    headers.add("git2/sys/${it.name}")
                }
            }

            val noStringConversion = listOf(
                "git_attr_value",
                "git_mailmap_from_buffer"
            )

            val constVar = listOf(
                """const char *git_attr__true  = "[internal]__TRUE__";""",
                """const char *git_attr__false = "[internal]__FALSE__";""",
                """const char *git_attr__unset = "[internal]__UNSET__";""",
            )

            val template = """
                |headers = ${headers.joinToString(" ")}
                |staticLibraries = libgit2.a libssh2.a libssl.a libcrypto.a
                |libraryPaths = ${
                libgit2DistDir.resolve("lib").normalize().absolutePath
            } ${libssh2DistDir.resolve("lib").normalize().absolutePath}${if (opensslDir != null) " $opensslDir/lib" else ""}
                |compilerOpts = -I${libgit2DistDir.resolve("include").normalize().absolutePath}
                |linkerOpts = ${inputs.files.singleFile.readText()}
                |
                |noStringConversion = ${noStringConversion.joinToString(" ")}
                |
                |---
                |
                |${constVar.joinToString("\n")}
            """.trimMargin()
            defFile.writeText(template)
        }
    }

    val configureLibnative by creating(Exec::class) {
        group = "libnative"
        inputs.files(
            libnativeSourceDir.resolve("native.h"),
            libnativeSourceDir.resolve("native.c"),
            libnativeSourceDir.resolve("CMakeLists.txt"),
        )
        outputs.dir(libnativeCMake)
        workingDir(libnativeCMake)
        val command = commandLine(
            "sh", "-c",
            "cmake $libnativeSourceDir -DCMAKE_INSTALL_PREFIX=$libnativeDist"
        )
        doLast {
            logger.warn("Configure native: ${command.executable} ${command.args?.joinToString(" ")}")
        }
    }

    val buildLibnative by creating(Exec::class) {
        group = "libnative"
        dependsOn(configureLibnative)
        inputs.dir(libnativeCMake)
        outputs.dir(libnativeDist)
        outputs.cacheIf { false }
        workingDir(libnativeCMake)

        val command = commandLine(
            "sh", "-c",
            "cmake --build . --target install"
        )
        doFirst {
            logger.warn("Build native: ${command.executable} ${command.args?.joinToString(" ")}")
        }
    }

    val generateLibnativeDef by creating {
        group = "interop"
        inputs.dir(libnativeDist)
        outputs.file(libnativeDef)
        dependsOn(buildLibnative)
        doLast {
            val template = """
                |headers = native.h
                |staticLibraries = libnative.a
                |compilerOpts = -I${libnativeDist.resolve("include").normalize().absolutePath}
                |libraryPaths = ${libnativeDist.resolve("lib").normalize().absolutePath}
            """.trimMargin()
            libnativeDef.writeText(template)
        }
    }

    val generateDef by creating {
        group = "interop"
        dependsOn(generateLibgit2Def, generateLibnativeDef)
    }
}
