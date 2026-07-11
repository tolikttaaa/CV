// The personal CV: content written in the cv-dsl DSL, plus the entry point
// that renders all artifacts into <repo root>/build.
//
// Pipeline tasks (group "cv", list with `./gradlew tasks --group cv`):
//   generateLatex — Kotlin DSL → build/latex (sources + template + photo)
//   generatePdf   — generateLatex + two LuaLaTeX passes → build/cv.pdf
//   generateWeb   — Kotlin DSL → build/web (generated HTML + browser assets)
//   assembleSite  — generateWeb + generatePdf → build/site (deployable bundle)
//   serveSite     — assembleSite + detached jwebserver on http://localhost:8080
//   stopSite      — kills the dev server
//
// Task actions only capture local vals (plain File/String) — never script
// members — to stay compatible with the configuration cache.
import java.net.InetSocketAddress
import java.net.Socket

plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
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

val generateLatex = tasks.register<JavaExec>("generateLatex") {
    group = "cv"
    description = "Generates the LaTeX source directory (build/latex) from the Kotlin DSL."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("cv.MainKt")
    workingDir = repoRoot
    args = listOf(".", "latex")
}

val generatePdf = tasks.register("generatePdf") {
    group = "cv"
    description = "Compiles the CV to build/cv.pdf with LuaLaTeX (two passes)."
    dependsOn(generateLatex)
    // Captured as locals: task actions must not reference script members
    // (configuration-cache compatibility).
    val latexDir = File(buildRoot, "latex")
    val logFile = File(buildRoot, "lualatex.log")
    val pdfFile = File(buildRoot, "cv.pdf")
    val lualatexBin = lualatex
    doLast {
        repeat(2) { pass ->
            val exit = ProcessBuilder(
                lualatexBin, "-interaction=nonstopmode", "-output-directory=..", "cv.tex",
            )
                .directory(latexDir)
                .redirectOutput(logFile)
                .redirectErrorStream(true)
                .start()
                .waitFor()
            check(exit == 0) { "lualatex pass ${pass + 1} failed (exit $exit) — see $logFile" }
        }
        println("Compiled $pdfFile")
    }
}

val generateWeb = tasks.register<JavaExec>("generateWeb") {
    group = "cv"
    description = "Generates static portfolio files in build/web from the Kotlin DSL."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("cv.MainKt")
    workingDir = repoRoot
    args = listOf(".", "web")
}

val assembleSite = tasks.register<Sync>("assembleSite") {
    group = "cv"
    description = "Assembles the generated portfolio and PDF into build/site."
    dependsOn(generateWeb, generatePdf)
    from(File(buildRoot, "web"))
    from(File(buildRoot, "cv.pdf"))
    into(File(buildRoot, "site"))
}

// Shell one-liner that kills whatever currently listens on the dev-server port.
// PATH is extended first: IDE-spawned Gradle daemons may lack /usr/sbin (lsof).
// A plain String constant so task actions can use it without referencing the
// build script object (configuration-cache compatibility).
val freePortCommand =
    "export PATH=\"\$PATH:/usr/sbin:/usr/bin:/bin\"; lsof -ti tcp:8080 | xargs kill -9 2>/dev/null; exit 0"

val serveSite = tasks.register("serveSite") {
    group = "cv"
    description = "Serves build/site on a temporary local server at http://localhost:8080."
    dependsOn(assembleSite)
    val siteDir = File(buildRoot, "site")
    val logFile = File(buildRoot, "site-server.log")
    val killCommand = freePortCommand
    doLast {
        // Free the port, then start the server fully detached (nohup + background
        // subshell) so it survives Gradle daemon restarts, e.g. by the IDE.
        ProcessBuilder("bash", "-c", killCommand).start().waitFor()
        // Serve with the JDK's own jwebserver (JDK 18+): unlike macOS's
        // /usr/bin/python3 it is a real binary, not an xcrun shim that breaks
        // in IDE environments with a stale DEVELOPER_DIR.
        val jwebserver = File(System.getProperty("java.home"), "bin/jwebserver")
        check(jwebserver.exists()) { "jwebserver not found in ${System.getProperty("java.home")} (needs JDK 18+)" }
        ProcessBuilder(
            "bash", "-c",
            "nohup '${jwebserver.absolutePath}' -b 127.0.0.1 -p 8080 -d '${siteDir.absolutePath}' " +
                "> '${logFile.absolutePath}' 2>&1 &",
        ).start().waitFor()

        // Verify the server actually answers before declaring success.
        val deadline = System.currentTimeMillis() + 10_000
        var up = false
        while (!up && System.currentTimeMillis() < deadline) {
            up = runCatching {
                Socket().use { it.connect(InetSocketAddress("127.0.0.1", 8080), 500) }
                true
            }.getOrDefault(false)
            if (!up) Thread.sleep(200)
        }
        check(up) {
            val log = if (logFile.exists()) logFile.readText().trim() else "(no log written)"
            "Dev server did not come up on port 8080.\nServer log ($logFile):\n$log"
        }
        println("Serving build/site at http://localhost:8080 (stop with: ./gradlew stopSite)")
    }
}

val stopSite = tasks.register("stopSite") {
    group = "cv"
    description = "Stops the temporary local server started by serveSite."
    val killCommand = freePortCommand
    doLast {
        ProcessBuilder("bash", "-c", killCommand).start().waitFor()
        println("Stopped the dev server on port 8080 (if it was running)")
    }
}
