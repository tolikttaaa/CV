package cv.render.web

import cv.model.Cv
import cv.render.CvRenderer
import java.nio.file.Files
import java.nio.file.Path

/** Public entry point for the reusable static web representation. */
object WebRenderer : CvRenderer {

    /** Generates `index.html` and extracts the browser assets into [outDir]. */
    override fun render(cv: Cv, outDir: Path) {
        require(cv.sections.isNotEmpty()) { "A web portfolio requires at least one section" }
        Files.createDirectories(outDir)
        outDir.resolve("index.html").toFile().writeText(cv.renderWebDocument())
        WebTemplate.extractTo(outDir)
    }
}
