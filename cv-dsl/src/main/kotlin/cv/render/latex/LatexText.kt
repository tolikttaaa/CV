package cv.render.latex

import cv.model.Bold
import cv.model.Colored
import cv.model.Inline
import cv.model.Italic
import cv.model.Link
import cv.model.NoWrap
import cv.model.Plain
import cv.model.RichText

/**
 * Converts inline markup and plain strings to LaTeX source fragments.
 */
internal object LatexText {

    /** Renders a [RichText] run as LaTeX source. */
    fun render(text: RichText): String = render(text.inlines)

    /** Renders a list of inline nodes as LaTeX source. */
    fun render(items: List<Inline>): String =
        items.joinToString("") { render(it) }

    private fun render(node: Inline): String = when (node) {
        is Plain -> escape(node.text)
        is Bold -> """\textbf{${render(node.content)}}"""
        is Italic -> """\emph{${render(node.content)}}"""
        is Colored -> """\textcolor{${node.color.latexName}}{${render(node.content)}}"""
        is Link -> """\link{${escapeUrl(node.url)}}{${render(node.content)}}"""
        is NoWrap -> """\mbox{${render(node.content)}}"""
    }

    /**
     * Escapes all LaTeX special characters in plain text, so the DSL content
     * can be written naturally (`&`, `%`, `_`, …).
     */
    fun escape(text: String): String = buildString {
        for (c in text) {
            when (c) {
                '\\' -> append("""\textbackslash{}""")
                '{' -> append("""\{""")
                '}' -> append("""\}""")
                '&' -> append("""\&""")
                '%' -> append("""\%""")
                '#' -> append("""\#""")
                '_' -> append("""\_""")
                '$' -> append("""\$""")
                '~' -> append("""\textasciitilde{}""")
                '^' -> append("""\textasciicircum{}""")
                else -> append(c)
            }
        }
    }

    /** Escapes only the characters that break a URL argument in LaTeX. */
    fun escapeUrl(url: String): String =
        url.replace("%", """\%""").replace("#", """\#""")
}
