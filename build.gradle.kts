import dev.detekt.gradle.Detekt
import dev.detekt.gradle.extensions.DetektExtension

// Shared build configuration. Plugin and toolchain versions are centralized in
// gradle/libs.versions.toml; module-specific tasks live in the module builds:
//   cv-dsl/build.gradle.kts — reusable CV toolkit (library)
//   my-cv/build.gradle.kts  — the actual CV + the artifact pipeline tasks
plugins {
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
