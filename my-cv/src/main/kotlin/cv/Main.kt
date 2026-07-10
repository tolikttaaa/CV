package cv

import cv.content.anatoliiCv
import cv.render.latex.LatexRenderer
import cv.render.web.WebRenderer
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

/**
 * Generates CV artifacts from the Kotlin DSL definition.
 *
 * Usage: `Main [root] [target]`
 *  - `root` — repository root the `build/` directory lives under (default: `.`)
 *  - `target` — what to generate:
 *      - `latex` — `build/latex/`: complete LaTeX source directory (generated
 *        sections, bundled document class and fonts, profile photo)
 *      - `web` — `build/cv-data.json`: portfolio data for the web view
 *      - `all` (default) — both
 *
 * Normally invoked through the Gradle tasks `generateLatex` / `generateWeb` / `run`.
 */
fun main(args: Array<String>) {
    val root = Path.of(args.getOrElse(0) { "." }).toAbsolutePath().normalize()
    val target = args.getOrElse(1) { "all" }
    require(target in setOf("latex", "web", "all")) {
        "Unknown target \"$target\" — expected latex, web or all"
    }

    if (target != "web") {
        val latexOut = root.resolve("build/latex")
        LatexRenderer.render(anatoliiCv, latexOut)
        anatoliiCv.photo?.let { copyPhoto(latexOut.resolve(it.file)) }
        println("Generated LaTeX sources in $latexOut")
    }

    if (target != "latex") {
        val jsonOut = root.resolve("build/cv-data.json")
        WebRenderer.render(anatoliiCv, jsonOut)
        println("Generated $jsonOut")
    }
}

/** Copies the bundled profile photo (a resource of this module) next to the LaTeX sources. */
private fun copyPhoto(target: Path) {
    val stream = object {}.javaClass.getResourceAsStream("/photo.jpg")
        ?: error("Bundled resource /photo.jpg not found")
    stream.use {
        Files.createDirectories(target.parent)
        Files.copy(it, target, StandardCopyOption.REPLACE_EXISTING)
    }
}
