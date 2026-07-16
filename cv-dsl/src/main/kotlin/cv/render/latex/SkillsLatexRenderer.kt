package cv.render.latex

import cv.model.SkillEntry
import cv.model.SkillsSection
import cv.render.ElementRenderer

/** Renders the skills keyword table. */
internal object LatexSkillsSectionRenderer : ElementRenderer<SkillsSection, Unit> {
    override fun render(element: SkillsSection, context: Unit): String = with(element) {
        buildString {
            append(renderLatexTitle())
            appendLine("""\begin{keywords}""")
            for (entry in entries) append(LatexRendererBundle.skillEntryRenderer.render(entry, context))
            appendLine("""\end{keywords}""")
        }
    }
}

internal object LatexSkillEntryRenderer : ElementRenderer<SkillEntry, Unit> {
    override fun render(element: SkillEntry, context: Unit): String = with(element) {
        """    \keywordsentry{${latexEscape(category)}}{${latexEscape(skills.joinToString(", "))}}""" + "\n"
    }
}
