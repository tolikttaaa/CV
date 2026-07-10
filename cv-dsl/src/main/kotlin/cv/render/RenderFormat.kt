package cv.render

/** Closed set of complete output representations provided by `cv-dsl`. */
sealed interface RenderFormat {
    data object Web : RenderFormat
    data object Latex : RenderFormat
}
