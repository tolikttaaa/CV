// Root build of the CV generator. Declares the Kotlin plugin version once for
// both modules; all real configuration lives in the module build scripts:
//   cv-dsl/build.gradle.kts — reusable CV toolkit (library)
//   my-cv/build.gradle.kts  — the actual CV + the artifact pipeline tasks
plugins {
    kotlin("jvm") version "2.2.20" apply false
}

subprojects {
    repositories {
        mavenCentral()
    }
}
