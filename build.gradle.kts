import dev.detekt.gradle.Detekt
import dev.detekt.gradle.extensions.DetektExtension

// Root coordinator for the personal content build and the included cv-dsl
// build. Versions live in gradle/libs.versions.toml; reusable CV tasks are
// supplied by cv-dsl's Gradle plugin rather than implemented here.
plugins {
    base
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.detekt) apply false
}

val detektVersion: String = libs.versions.detekt.get()

subprojects {
    apply(plugin = "dev.detekt")

    repositories {
        mavenCentral()
    }

    extensions.configure<DetektExtension> {
        // Use the plugin's defaults as the common policy. Declaration-level
        // suppressions document the few intentional DSL/template exceptions.
        toolVersion = detektVersion
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
}

// Composite builds are isolated by Gradle, so the root verification lifecycle
// explicitly includes the reusable cv-dsl build as well as the content module.
tasks.named("check") {
    dependsOn(":my-cv:check")
    dependsOn(gradle.includedBuild("cv-dsl").task(":check"))
}

tasks.register("detekt") {
    group = "verification"
    description = "Runs Detekt in my-cv and the included cv-dsl build."
    dependsOn(":my-cv:detekt")
    dependsOn(gradle.includedBuild("cv-dsl").task(":detekt"))
}
