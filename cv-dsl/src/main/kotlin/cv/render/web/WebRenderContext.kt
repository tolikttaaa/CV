package cv.render.web

import cv.model.Cv

/** Shared state available to web section and leaf renderers. */
internal data class WebRenderContext(val cv: Cv)
