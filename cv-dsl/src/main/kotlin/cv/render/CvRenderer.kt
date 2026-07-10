package cv.render

import cv.model.Cv
import java.nio.file.Path

/**
 * Common contract for complete CV representations.
 *
 * Implementations choose their own directory layout beneath [outDir].
 */
fun interface CvRenderer {
    fun render(cv: Cv, outDir: Path)
}
