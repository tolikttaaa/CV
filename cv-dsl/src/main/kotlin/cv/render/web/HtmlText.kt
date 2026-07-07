package cv.render.web

import cv.model.Bold
import cv.model.Bullets
import cv.model.Colored
import cv.model.Description
import cv.model.Inline
import cv.model.Italic
import cv.model.Link
import cv.model.NoWrap
import cv.model.Paragraph
import cv.model.Plain
import cv.model.RichText

/**
 * Converts inline and block markup to the HTML fragments embedded in
 * `cv-data.json` (rendered by the web app via `innerHTML`).
 */
internal object HtmlText {

    /**
     * Renders block content as one HTML string: paragraphs joined with a space,
     * bullet lists as `<ul><li>…</li></ul>`.
     */
    fun html(description: Description): String =
        description.blocks.joinToString(" ") { block ->
            when (block) {
                is Paragraph -> html(block.text)
                is Bullets -> block.items.joinToString(
                    separator = "",
                    prefix = "<ul>",
                    postfix = "</ul>",
                ) { "<li>${html(it)}</li>" }
            }
        }.trim()

    /** Renders a [RichText] run as an HTML fragment. */
    fun html(text: RichText): String = html(text.inlines)

    private fun html(items: List<Inline>): String =
        items.joinToString("") { html(it) }.normalizeSpace()

    private fun html(node: Inline): String = when (node) {
        is Plain -> escape(node.text)
        is Bold -> "<strong>${html(node.content)}</strong>"
        is Italic -> "<em>${html(node.content)}</em>"
        is Colored -> """<span style="color:${node.color.css}">${html(node.content)}</span>"""
        is Link -> """<a href="${escape(node.url)}" target="_blank" rel="noopener">${html(node.content)}</a>"""
        is NoWrap -> html(node.content) // line-breaking hints are meaningless on the web
    }

    /** Renders a [RichText] run as plain text, dropping all styling. */
    fun plain(text: RichText): String =
        plain(text.inlines).normalizeSpace()

    private fun plain(items: List<Inline>): String =
        items.joinToString("") { node ->
            when (node) {
                is Plain -> node.text
                is Bold -> plain(node.content)
                is Italic -> plain(node.content)
                is Colored -> plain(node.content)
                is Link -> plain(node.content)
                is NoWrap -> plain(node.content)
            }
        }

    /** Escapes text for safe embedding in HTML content and attribute values. */
    fun escape(s: String): String = s
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")

    private fun String.normalizeSpace(): String =
        replace(Regex("\\s+"), " ").trim()
}
