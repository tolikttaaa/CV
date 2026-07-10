rootProject.name = "cv-generator"

// Reusable CV DSL library: data model, builders, LaTeX and web renderers.
include("cv-dsl")

// The actual CV: content written in the DSL plus the generator entry point.
include("my-cv")
