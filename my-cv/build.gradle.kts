// Personal content module. All artifact-pipeline tasks come from the reusable
// cv.dsl.generation plugin provided by the included cv-dsl build.
plugins {
    alias(libs.plugins.kotlin.jvm)
    application
    id("cv.dsl.generation")
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}

dependencies {
    implementation(libs.cv.dsl)
}

application {
    mainClass.set("cv.MainKt")
}

tasks.named<JavaExec>("run") {
    // Generate into <repo root>/build by default; override with --args
    workingDir = rootDir
}
