// Reusable, content-free CV toolkit: the data model, the `cv { … }` DSL,
// and the LaTeX / web renderers. Intended to be extractable into a
// standalone library project later.
plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}
