package cv.render.latex

import cv.model.EducationEntry
import cv.model.EducationSection
import cv.render.ElementRenderer

/** Renders the education timeline. */
internal object LatexEducationSectionRenderer : ElementRenderer<EducationSection, Unit> {
    override fun render(element: EducationSection, context: Unit): String = with(element) {
        buildString {
            append(renderLatexTitle())
            appendLine("""\begin{education}""")
            for (entry in entries) append(LatexRendererBundle.educationEntryRenderer.render(entry, context))
            appendLine("""\end{education}""")
        }
    }
}

internal object LatexEducationEntryRenderer : ElementRenderer<EducationEntry, Unit> {
    override fun render(element: EducationEntry, context: Unit): String = with(element) {
        """    \educationentry{${latexEscape(years)}}{${LatexText.render(description)}}""" + "\n"
    }
}
