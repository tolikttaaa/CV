package cv.render.latex

import cv.model.Project
import cv.model.ProjectsSection
import cv.render.ElementRenderer

/** Renders the projects' collection and individual project entries. */
internal object LatexProjectsSectionRenderer : ElementRenderer<ProjectsSection, Unit> {
    override fun render(element: ProjectsSection, context: Unit): String = with(element) {
        buildString {
            append(renderLatexTitle())
            appendLine("""\begin{projects}""")
            for (project in projects) append(LatexRendererBundle.projectRenderer.render(project, context))
            appendLine("""\end{projects}""")
        }
    }
}

internal object LatexProjectRenderer : ElementRenderer<Project, Unit> {
    override fun render(element: Project, context: Unit): String = with(element) {
        buildString {
            appendLine("""    \project""")
            appendLine("""        {${latexEscape(name)}}""")
            appendLine("""        {${company.renderLatex(emphasized = true)}}""")
            appendLine("""        {${latexEscape(dates)}}""")
            appendLine("""        {""")
            append(description.renderLatexBlocks(indent = 3))
            appendLine("""        }""")
            appendLine("""        {${latexEscape(tags.joinToString(", "))}}""")
        }
    }
}
