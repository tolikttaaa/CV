package cv

import cv.content.anatoliiCv
import cv.render.latex.LatexRenderer
import cv.render.web.WebRenderer
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

/**
 * Generates all CV artifacts from the Kotlin DSL definition:
 *  - `build/latex/` — complete LaTeX source directory (generated sections,
 *    bundled document class and fonts, profile photo), ready for `lualatex`
 *  - `build/cv-data.json` — portfolio data for the web view
 *
 * Usage: run from the repository root (default), or pass the repo root as the first argument.
 */
fun main(args: Array<String>) {
    val root = Path.of(args.getOrElse(0) { "." }).toAbsolutePath().normalize()

    val latexOut = root.resolve("build/latex")
    LatexRenderer.render(anatoliiCv, latexOut)
    copyPhoto(latexOut.resolve(anatoliiCv.photo))
    println("Generated LaTeX sources in $latexOut")

    val jsonOut = root.resolve("build/cv-data.json")
    WebRenderer.render(anatoliiCv, jsonOut)
    println("Generated $jsonOut")
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
