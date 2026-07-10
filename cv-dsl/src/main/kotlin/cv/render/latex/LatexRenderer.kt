package cv.render.latex

import cv.model.Cv
import cv.render.CvRenderer
import cv.render.renderWith
import java.nio.file.Files
import java.nio.file.Path

/** Public entry point for the reusable LaTeX representation. */
object LatexRenderer : CvRenderer {

    /** Writes `cv.tex`, all section files, and the bundled template into [outDir]. */
    override fun render(cv: Cv, outDir: Path) {
        Files.createDirectories(outDir.resolve("sections"))
        LatexTemplate.extractTo(outDir)
        outDir.resolve("cv.tex").toFile().writeText(cv.renderLatexDocument())
        for (section in cv.sections) {
            outDir.resolve("sections/${section.id}.tex").toFile()
                .writeText(section.renderWith(LatexRendererBundle, Unit))
        }
    }
}
