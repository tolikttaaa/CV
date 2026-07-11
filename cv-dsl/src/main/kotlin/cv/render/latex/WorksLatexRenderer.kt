package cv.render.latex

import cv.model.Bullets
import cv.model.Work
import cv.model.WorksSection
import cv.render.ElementRenderer

/** Renders employment and teaching sections, which share the works layout. */
internal object LatexWorksSectionRenderer : ElementRenderer<WorksSection, Unit> {
    override fun render(element: WorksSection, context: Unit): String = with(element) {
        buildString {
            append(renderLatexTitle())
            if (works.any { work -> work.description.blocks.any { it is Bullets } }) {
                appendLine("""\renewcommand{\labelitemi}{${'$'}\bullet${'$'}}""")
            }
            appendLine("""\begin{works}""")
            for (work in works) append(LatexRendererBundle.workRenderer.render(work, context))
            appendLine("""\end{works}""")
        }
    }
}

internal object LatexWorkRenderer : ElementRenderer<Work, Unit> {
    override fun render(element: Work, context: Unit): String = with(element) {
        buildString {
            val renderedCompany = company.renderLatex(emphasized = true)
            appendLine("""    \work""")
            appendLine(
                """        {${latexEscape(role)}} {$renderedCompany} """ +
                    """{${latexEscape(location)}} {${latexEscape(dates)}}""",
            )
            appendLine("""        {""")
            append(description.renderLatexBlocks(indent = 3))
            appendLine("""        }""")
            appendLine("""        {${latexEscape(tags.joinToString(", "))}}""")
        }
    }
}
