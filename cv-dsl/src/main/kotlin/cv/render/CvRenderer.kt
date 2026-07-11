package cv.render

import cv.model.Cv
import java.nio.file.Path

/**
 * Common contract for complete CV representations.
 *
 * Implementations choose their own directory layout beneath [outDir] and may
 * extract bundled resources alongside generated source files. Callers supply
 * format-independent content through [cv].
 */
fun interface CvRenderer {
    /**
     * Renders [cv] into [outDir], creating or replacing representation-owned
     * files without assuming that the directory is otherwise empty.
     */
    fun render(cv: Cv, outDir: Path)
}
