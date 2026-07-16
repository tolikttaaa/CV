package cv.render.latex

import cv.model.Bullets
import cv.model.Description
import cv.model.Organization
import cv.model.Paragraph
import cv.model.Section
import cv.model.Social
import cv.render.ElementRenderer

internal fun Section.renderLatexTitle(): String =
    """\sectionTitle{${latexEscape(title)}}{\$icon}""" + "\n"

internal object LatexSocialRenderer : ElementRenderer<Social, Unit> {
    override fun render(element: Social, context: Unit): String = when (element) {
        is Social.Phone -> """\smartphone{${latexEscape(element.number)}}"""
        is Social.Telegram -> """\telegram{${latexEscape(element.handle)}}"""
        is Social.Email -> """\email{${latexEscape(element.address)}}"""
        is Social.LinkedIn -> """\linkedin{${latexEscape(element.handle)}}"""
        is Social.LeetCode -> """\leetcode{${latexEscape(element.handle)}}"""
        is Social.GitHub -> """\github{${latexEscape(element.handle)}}"""
        is Social.Address -> """\address{${latexEscape(element.text)}}"""
    }
}

internal fun Organization.renderLatex(emphasized: Boolean): String {
    val label = if (emphasized) """\textbf{${latexEscape(name)}}""" else latexEscape(name)
    return url?.let { """\link{${LatexText.escapeUrl(it)}}{$label}""" } ?: label
}

internal fun Description.renderLatexBlocks(indent: Int): String = buildString {
    val pad = "    ".repeat(indent)
    for ((index, block) in blocks.withIndex()) {
        if (index > 0) appendLine()
        when (block) {
            is Paragraph -> appendLine(pad + LatexText.render(block.text))
            is Bullets -> {
                appendLine("""$pad\begin{itemize}""")
                for (item in block.items) {
                    appendLine("""$pad    \item {${LatexText.render(item)}}""")
                }
                appendLine("""$pad\end{itemize}""")
            }
        }
    }
}

internal fun latexEscape(value: String): String = LatexText.escape(value)
