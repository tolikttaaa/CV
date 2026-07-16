pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "cv-generator"

// Application module: personal content, photo, generation CLI and artifact tasks.
include("my-cv")
