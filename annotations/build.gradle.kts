plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()

    // val hostOs = System.getProperty("os.name")
    // val isMingwX64 = hostOs.startsWith("Windows")
    // val (nativeTarget, nativeTargetString) = when {
    //     hostOs == "Mac OS X" -> {
    //         if (System.getProperty("os.arch").contains("aarch64")) {
    //             macosArm64("native") to "macosArm64"
    //         } else {
    //             macosX64("native") to "macosX64"
    //         }
    //     }
    //     hostOs == "Linux" -> linuxX64("native") to "linuxX64"
    //     isMingwX64 -> mingwX64("native") to "mingwX64"
    //     else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    // }
    listOf(
        macosArm64(),
        macosX64(),
        linuxX64(),
        mingwX64(),
    )

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

tasks.whenTaskAdded {
    if (group == "verification") {
        enabled = false
    }
}
