plugins {
    kotlin("jvm") apply false
    kotlin("multiplatform") apply false
}

allprojects {
    group = "com.kgit2"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenLocal()
        mavenCentral()
    }
}
