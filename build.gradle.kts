import dev.detekt.gradle.Detekt
import dev.detekt.gradle.extensions.DetektExtension

// Root coordinator for the personal content build. Build-tool versions live in
// gradle/libs.versions.toml; reusable CV tasks come from the released cv-dsl.
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
        maven("https://jitpack.io")
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

tasks.named("check") {
    dependsOn(":my-cv:check")
}

tasks.register("detekt") {
    group = "verification"
    description = "Runs Detekt on the personal CV content module."
    dependsOn(":my-cv:detekt")
}
