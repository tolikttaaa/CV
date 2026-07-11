rootProject.name = "cv-generator"

// Reusable CV DSL library: model, builders, renderer contracts and both formats.
include("cv-dsl")

// Application module: personal content, photo, generation CLI and artifact tasks.
include("my-cv")
