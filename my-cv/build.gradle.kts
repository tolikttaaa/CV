// Personal content module. The released cv-dsl artifact supplies both the
// public DSL and the cv.dsl.generation Gradle plugin.
buildscript {
    repositories {
        maven("https://jitpack.io")
    }
    dependencies {
        val cvDslVersion = providers.gradleProperty("cvDslVersion").get()
        classpath("com.github.tolikttaaa:cv-dsl:$cvDslVersion")
    }
}

plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

apply(plugin = "cv.dsl.generation")

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}

dependencies {
    implementation("com.github.tolikttaaa:cv-dsl:${providers.gradleProperty("cvDslVersion").get()}")
}

application {
    mainClass.set("cv.MainKt")
}

tasks.named<JavaExec>("run") {
    // Generate into <repo root>/build by default; override with --args
    workingDir = rootDir
}
