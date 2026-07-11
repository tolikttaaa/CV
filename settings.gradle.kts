pluginManagement {
    // The reusable DSL build provides the generation plugin consumed by my-cv.
    includeBuild("cv-dsl")
}

rootProject.name = "cv-generator"

includeBuild("cv-dsl")

// Application module: personal content, photo, generation CLI and artifact tasks.
include("my-cv")
