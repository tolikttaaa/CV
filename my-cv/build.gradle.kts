// The personal CV: content written in the cv-dsl DSL, plus the entry point
// that renders all artifacts into <repo root>/build.
plugins {
    kotlin("jvm")
    application
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":cv-dsl"))
}

application {
    mainClass.set("cv.MainKt")
}

tasks.named<JavaExec>("run") {
    // Generate into <repo root>/build by default; override with --args
    workingDir = rootDir
}
