package cv.render.web

import cv.model.Cv

/**
 * Shared immutable state available while producing one web document.
 * Leaf renderers receive the same context so future page-level options do not
 * require changes to the generic [cv.render.ElementRenderer] contract.
 */
internal data class WebRenderContext(val cv: Cv)
