package cv.render.latex

import cv.model.SummarySection
import cv.render.ElementRenderer

/** Renders the free-form summary section. */
internal object LatexSummarySectionRenderer : ElementRenderer<SummarySection, Unit> {
    override fun render(element: SummarySection, context: Unit): String = with(element) {
        buildString {
            append(renderLatexTitle())
            appendLine("""\begin{summary}""")
            appendLine("""    \summaryText{""")
            append(text.renderLatexBlocks(indent = 2))
            appendLine("""    }""")
            appendLine("""\end{summary}""")
        }
    }
}
