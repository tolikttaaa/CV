package cv.render

/**
 * Renders one model [E] to a source fragment using format-specific context [C].
 * Complete format implementations expose these renderers through
 * [RendererBundle], which guarantees that corresponding web and LaTeX
 * implementations are registered.
 */
fun interface ElementRenderer<E, C> {
    /** Renders [element] without mutating it or [context]. */
    fun render(element: E, context: C): String
}
