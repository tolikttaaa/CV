package cv.render

import cv.render.latex.LatexRenderer
import cv.render.web.WebRenderer

/** Public composition root for the renderer implementations bundled with `cv-dsl`. */
object CvRendererFactory {
    /** Returns the singleton renderer for [format]. */
    fun create(format: RenderFormat): CvRenderer = when (format) {
        RenderFormat.Web -> WebRenderer
        RenderFormat.Latex -> LatexRenderer
    }
}
