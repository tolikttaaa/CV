package cv.render

import cv.render.latex.LatexRenderer
import cv.render.web.WebRenderer

/** Resolves a complete renderer for one of the supported output formats. */
object CvRendererFactory {
    fun create(format: RenderFormat): CvRenderer = when (format) {
        RenderFormat.Web -> WebRenderer
        RenderFormat.Latex -> LatexRenderer
    }
}
