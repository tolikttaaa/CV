// The personal CV: content written in the cv-dsl DSL, plus the entry point
// that renders all artifacts into <repo root>/build.
//
// Pipeline tasks (group "cv"):
//   generateLatex → generatePdf              — PDF artifact (build/cv.pdf)
//   generateWeb   → assembleSite             — web page bundle (build/site)
//   serveSite                                — temporary dev server on :8080
plugins {
    kotlin("jvm")
    application
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":cv-dsl"))
}

application {
    mainClass.set("cv.MainKt")
}

tasks.named<JavaExec>("run") {
    // Generate into <repo root>/build by default; override with --args
    workingDir = rootDir
}

// ── CV pipeline ───────────────────────────────────────────────────────────────

val repoRoot: File = rootDir
val buildRoot = File(repoRoot, "build")

// LuaLaTeX binary: -PlualatexPath=... overrides; defaults to the MacTeX
// location when present, otherwise whatever `lualatex` resolves to on PATH.
val lualatex: String = (findProperty("lualatexPath") as String?)
    ?: "/Library/TeX/texbin/lualatex".takeIf { File(it).exists() }
    ?: "lualatex"

val generateLatex by tasks.registering(JavaExec::class) {
    group = "cv"
    description = "Generates the LaTeX source directory (build/latex) from the Kotlin DSL."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("cv.MainKt")
    workingDir = repoRoot
    args = listOf(".", "latex")
}

val generatePdf by tasks.registering {
    group = "cv"
    description = "Compiles the CV to build/cv.pdf with LuaLaTeX (two passes)."
    dependsOn(generateLatex)
    doLast {
        val latexDir = File(buildRoot, "latex")
        repeat(2) { pass ->
            val exit = ProcessBuilder(
                lualatex, "-interaction=nonstopmode", "-output-directory=..", "cv.tex",
            )
                .directory(latexDir)
                .redirectOutput(File(buildRoot, "lualatex.log"))
                .redirectErrorStream(true)
                .start()
                .waitFor()
            check(exit == 0) {
                "lualatex pass ${pass + 1} failed (exit $exit) — see ${File(buildRoot, "lualatex.log")}"
            }
        }
        println("Compiled ${File(buildRoot, "cv.pdf")}")
    }
}

val generateWeb by tasks.registering(JavaExec::class) {
    group = "cv"
    description = "Generates the portfolio data (build/cv-data.json) from the Kotlin DSL."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("cv.MainKt")
    workingDir = repoRoot
    args = listOf(".", "web")
}

val assembleSite by tasks.registering(Sync::class) {
    group = "cv"
    description = "Assembles the complete web page (build/site): static app, data, PDF, photo."
    dependsOn(generateWeb, generatePdf)
    from(File(repoRoot, "web"))
    from(File(buildRoot, "cv.pdf"))
    from(File(buildRoot, "cv-data.json"))
    from(File(buildRoot, "latex/photo.jpg"))
    into(File(buildRoot, "site"))
}

// Kills whatever currently listens on the dev-server port.
fun freePort(port: Int) {
    ProcessBuilder("bash", "-c", "lsof -ti tcp:$port | xargs kill -9 2>/dev/null; exit 0")
        .start().waitFor()
}

val serveSite by tasks.registering {
    group = "cv"
    description = "Serves build/site on a temporary local server at http://localhost:8080."
    dependsOn(assembleSite)
    doLast {
        // Free the port, then start a detached server that outlives the build.
        freePort(8080)
        ProcessBuilder("python3", "-m", "http.server", "8080")
            .directory(File(buildRoot, "site"))
            .redirectOutput(ProcessBuilder.Redirect.DISCARD)
            .redirectError(ProcessBuilder.Redirect.DISCARD)
            .start()
        println("Serving build/site at http://localhost:8080 (stop with: ./gradlew stopSite)")
    }
}

val stopSite by tasks.registering {
    group = "cv"
    description = "Stops the temporary local server started by serveSite."
    doLast {
        freePort(8080)
        println("Stopped the dev server on port 8080 (if it was running)")
    }
}
