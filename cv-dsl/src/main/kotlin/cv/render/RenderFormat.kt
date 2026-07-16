package cv.render

/** Closed set of complete output representations provided by `cv-dsl`. */
sealed interface RenderFormat {
    /** Static HTML portfolio plus reusable browser assets. */
    data object Web : RenderFormat

    /** Compilable LaTeX source tree plus document-class resources. */
    data object Latex : RenderFormat
}
