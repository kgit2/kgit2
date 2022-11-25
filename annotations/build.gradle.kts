plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()

    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosArm64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    // nativeTarget.apply {
    //     compilations.getByName("main") {
    //         cinterops {
    //             val libgit2 by creating {
    //                 defFile(rootProject.file("lib/libgit2.def"))
    //                 packageName("libgit2")
    //             }
    //         }
    //     }
    //     binaries {
    //         executable {
    //             entryPoint = "main"
    //         }
    //     }
    // }
}
