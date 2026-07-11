package cv.dsl

import cv.model.Bold
import cv.model.Bullets
import cv.model.Colored
import cv.model.CvColor
import cv.model.Description
import cv.model.Inline
import cv.model.Italic
import cv.model.Link
import cv.model.NoWrap
import cv.model.Paragraph
import cv.model.Plain
import cv.model.RichText

/**
 * Scope marker for the CV DSL: prevents accidentally calling methods of an
 * outer builder from inside a nested lambda.
 */
@DslMarker
annotation class CvDsl

/**
 * Builder for a run of inline markup ([RichText]).
 *
 * ```kotlin
 * richText {
 *     +"plain text with a "
 *     bold("bold word")
 *     +", a "
 *     link("https://example.com", "hyperlink")
 *     +" and "
 *     nowrap("unbreakable")
 *     +" text"
 * }
 * ```
 *
 * Styles nest: `bold { +"Data "; nowrap("Structures") }`.
 * Text is written as-is — LaTeX/HTML escaping is done by the renderers.
 */
@CvDsl
@Suppress("TooManyFunctions") // A fluent text DSL intentionally exposes one function per operation.
class TextBuilder {
    private val inlines = mutableListOf<Inline>()

    /** Appends plain text: `+"some text"`. */
    operator fun String.unaryPlus() {
        inlines += Plain(this)
    }

    /** Appends [text] in bold. */
    fun bold(text: String) = bold { +text }

    /** Appends nested inline content in bold. */
    fun bold(block: TextBuilder.() -> Unit) {
        inlines += Bold(nested(block))
    }

    /** Appends [text] in italics. */
    fun italic(text: String) = italic { +text }

    /** Appends nested inline content in italics. */
    fun italic(block: TextBuilder.() -> Unit) {
        inlines += Italic(nested(block))
    }

    /** Appends [text] rendered in the given theme [color]. */
    fun colored(color: CvColor, text: String) = colored(color) { +text }

    /** Appends nested inline content rendered in the given theme [color]. */
    fun colored(color: CvColor, block: TextBuilder.() -> Unit) {
        inlines += Colored(color, nested(block))
    }

    /** Appends a hyperlink to [url] labeled with [label]. */
    fun link(url: String, label: String) = link(url) { +label }

    /** Appends a hyperlink to [url] whose label is nested inline content. */
    fun link(url: String, block: TextBuilder.() -> Unit) {
        inlines += Link(url, nested(block))
    }

    /** Appends [text] that must stay on one line (LaTeX `\mbox`). */
    fun nowrap(text: String) = nowrap { +text }

    /** Appends nested inline content that must stay on one line. */
    fun nowrap(block: TextBuilder.() -> Unit) {
        inlines += NoWrap(nested(block))
    }

    /** Finalizes the builder into an immutable [RichText]. */
    fun build() = RichText(inlines.toList())

    private fun nested(block: TextBuilder.() -> Unit): List<Inline> =
        TextBuilder().apply(block).build().inlines
}

/** Builds a [RichText] from inline markup. */
fun richText(block: TextBuilder.() -> Unit): RichText = TextBuilder().apply(block).build()

/** Wraps unstyled [text] into a [RichText]. */
fun plainText(text: String): RichText = RichText(listOf(Plain(text)))

/**
 * Builder for the items of a bullet list.
 */
@CvDsl
class BulletsBuilder {
    internal val items = mutableListOf<RichText>()

    /**
     * Adds a bullet. [text] may be a multiline string — indentation and line
     * breaks are collapsed to single spaces. Formatting is declared separately
     * in [highlights] (see [HighlightsBuilder]).
     */
    fun item(text: String, highlights: HighlightsBuilder.() -> Unit = {}) {
        items += highlightedText(text, highlights)
    }
}

/**
 * Builder for block-level body text ([Description]): any sequence of
 * paragraphs and bullet lists, in source order.
 *
 * ```kotlin
 * paragraph(
 *     """
 *     What the role was about, written as a natural
 *     multiline string.
 *     """,
 * )
 * bullets {
 *     item("Achievement with a highlighted metric.") {
 *         bold("highlighted metric")
 *     }
 * }
 * ```
 */
@CvDsl
class DescriptionBuilder {
    private val blocks = mutableListOf<cv.model.Block>()

    /**
     * Adds a paragraph. [text] may be a multiline string — indentation and line
     * breaks are collapsed to single spaces. Formatting is declared separately
     * in [highlights] (see [HighlightsBuilder]).
     */
    fun paragraph(text: String, highlights: HighlightsBuilder.() -> Unit = {}) {
        blocks += Paragraph(highlightedText(text, highlights))
    }

    /** Adds an unordered bullet list. */
    fun bullets(block: BulletsBuilder.() -> Unit) {
        blocks += Bullets(BulletsBuilder().apply(block).items.toList())
    }

    /** Finalizes the builder into an immutable [Description]. */
    fun build() = Description(blocks.toList())
}
