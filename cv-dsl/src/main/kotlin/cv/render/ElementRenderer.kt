package cv.render

/** Common contract for rendering one model element in a format-specific context. */
fun interface ElementRenderer<E, C> {
    fun render(element: E, context: C): String
}
