package cv.render.latex

import cv.model.Referee
import cv.model.ReferencesSection
import cv.render.ElementRenderer

/** Renders the references collection and individual referee entries. */
internal object LatexReferencesSectionRenderer : ElementRenderer<ReferencesSection, Unit> {
    override fun render(element: ReferencesSection, context: Unit): String = with(element) {
        buildString {
            append(renderLatexTitle())
            appendLine("""\begin{referees}""")
            for (referee in referees) append(LatexRendererBundle.refereeRenderer.render(referee, context))
            appendLine("""\end{referees}""")
        }
    }
}

internal object LatexRefereeRenderer : ElementRenderer<Referee, Unit> {
    override fun render(element: Referee, context: Unit): String = with(element) {
        buildString {
            appendLine("""    \referee""")
            appendLine("""        {${latexEscape(name)}}""")
            appendLine("""        {${latexEscape(role)}}""")
            appendLine("""        {${company.renderLatex(emphasized = false)}} {${latexEscape(period)}}""")
            appendLine("""        {${latexEscape(email)}}""")
        }
    }
}
