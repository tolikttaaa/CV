import dev.detekt.gradle.Detekt
import dev.detekt.gradle.extensions.DetektExtension

// Standalone reusable toolkit and Gradle plugin. Consumers depend on the
// library for the model/DSL and apply cv.dsl.generation for artifact tasks.
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt)
    `java-gradle-plugin`
}

group = "cv.dsl"
version = libs.versions.cv.dsl.get()

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}

gradlePlugin {
    plugins {
        create("cvGeneration") {
            id = "cv.dsl.generation"
            implementationClass = "cv.gradle.CvGenerationPlugin"
            displayName = "CV DSL generation"
            description = "Registers reusable LaTeX, PDF, web-site and preview tasks for a cv-dsl consumer."
        }
    }
}

extensions.configure<DetektExtension> {
    toolVersion = libs.versions.detekt.get()
    buildUponDefaultConfig = true
    parallel = true
}

tasks.withType<Detekt>().configureEach {
    reports {
        checkstyle.required.set(true)
        html.required.set(true)
        sarif.required.set(true)
        markdown.required.set(true)
    }
}
