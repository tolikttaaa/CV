package cv.dsl

import cv.model.CvColor
import cv.model.Inline
import cv.model.Plain
import cv.model.RichText

/**
 * A single formatting effect that a highlight rule can apply to a match.
 * Combine several in one rule: the first listed becomes the outermost wrapper,
 * e.g. `highlight("ITMO", linkTo(url), bold)` produces a link containing bold text.
 */
sealed interface Style {
    /** Bold emphasis. */
    data object Bold : Style

    /** Italic emphasis. */
    data object Italic : Style

    /** Keep the match on one line (LaTeX `\mbox`). */
    data object NoWrap : Style

    /** Render the match in a theme color. */
    data class Colored(val color: CvColor) : Style

    /** Wrap the match in a hyperlink. */
    data class Link(val url: String) : Style
}

/** One highlighting rule: where to apply ([pattern]) and what to apply ([styles]). */
internal class HighlightRule(val pattern: Regex, val styles: List<Style>)

/**
 * Builder for the highlight rules of one text block.
 *
 * The text itself stays plain; formatting is declared separately as rules that
 * match by literal substring or [Regex] and apply one or more [Style] modifiers:
 *
 * ```kotlin
 * paragraph(
 *     """
 *     Worked in multiple teams at Azul, Yandex, and Tinkoff,
 *     contributing to mission-critical services.
 *     """,
 * ) {
 *     bold("Azul")                                  // shorthand, single style
 *     highlight("Yandex", linkTo("https://yandex.com"), bold)  // combined styles
 *     bold(Regex("Tink\\w+"))                       // regex match
 * }
 * ```
 *
 * Every rule must match at least once — a rule that matches nothing fails the
 * build, so highlights cannot silently break when the text is reworded.
 * Rules apply to **all** occurrences of their match, in declaration order, and
 * only to still-unstyled text (a later rule never restyles an earlier match).
 */
@CvDsl
class HighlightsBuilder {
    internal val rules = mutableListOf<HighlightRule>()

    // ── Style modifiers for [highlight] ───────────────────────────────────────

    /** Bold modifier. */
    val bold: Style get() = Style.Bold

    /** Italic modifier. */
    val italic: Style get() = Style.Italic

    /** Keep-on-one-line modifier (LaTeX `\mbox`). */
    val nowrap: Style get() = Style.NoWrap

    /** Theme-color modifier. */
    fun colored(color: CvColor): Style = Style.Colored(color)

    /** Hyperlink modifier. */
    fun linkTo(url: String): Style = Style.Link(url)

    // ── Rules ─────────────────────────────────────────────────────────────────

    /** Applies [styles] (first = outermost) to every occurrence of the literal [text]. */
    fun highlight(text: String, vararg styles: Style) =
        highlight(Regex(Regex.escape(text)), *styles)

    /** Applies [styles] (first = outermost) to every match of [pattern]. */
    fun highlight(pattern: Regex, vararg styles: Style) {
        require(styles.isNotEmpty()) { "highlight(${pattern.pattern}) needs at least one style" }
        rules += HighlightRule(pattern, styles.toList())
    }

    // ── Single-style shorthands ───────────────────────────────────────────────

    /** Bolds every occurrence of the literal [text]. */
    fun bold(text: String) = highlight(text, bold)

    /** Bolds every match of [pattern]. */
    fun bold(pattern: Regex) = highlight(pattern, bold)

    /** Italicizes every occurrence of the literal [text]. */
    fun italic(text: String) = highlight(text, italic)

    /** Italicizes every match of [pattern]. */
    fun italic(pattern: Regex) = highlight(pattern, italic)

    /** Keeps every occurrence of the literal [text] on one line. */
    fun nowrap(text: String) = highlight(text, nowrap)

    /** Renders every occurrence of the literal [text] in the given theme [color]. */
    fun colored(color: CvColor, text: String) = highlight(text, colored(color))

    /** Turns every occurrence of the literal [text] into a hyperlink to [url]. */
    fun link(text: String, url: String) = highlight(text, linkTo(url))
}

/**
 * Builds a [RichText] from a (possibly multiline) string and highlight rules.
 *
 * The text is normalized first — indentation trimmed, line breaks and runs of
 * whitespace collapsed to single spaces — so content can be written as natural
 * Kotlin multiline literals.
 */
internal fun highlightedText(text: String, block: HighlightsBuilder.() -> Unit): RichText {
    val rules = HighlightsBuilder().apply(block).rules
    val normalized = normalize(text)
    var nodes: List<Inline> = listOf(Plain(normalized))
    for (rule in rules) {
        var matches = 0
        nodes = nodes.flatMap { node ->
            if (node is Plain) applyRule(node.text, rule) { matches++ } else listOf(node)
        }
        check(matches > 0) {
            "Highlight pattern \"${rule.pattern.pattern}\" matched nothing in: \"$normalized\""
        }
    }
    return RichText(nodes)
}

private fun normalize(text: String): String =
    text.trimIndent().replace(Regex("\\s+"), " ").trim()

/** Splits [text] around the matches of [rule], wrapping each match in the rule's styles. */
private fun applyRule(text: String, rule: HighlightRule, onMatch: () -> Unit): List<Inline> {
    val out = mutableListOf<Inline>()
    var consumed = 0
    for (match in rule.pattern.findAll(text)) {
        if (match.value.isEmpty()) continue
        onMatch()
        if (match.range.first > consumed) out += Plain(text.substring(consumed, match.range.first))
        out += rule.styles.foldRight(listOf<Inline>(Plain(match.value))) { style, inner ->
            listOf(style.wrap(inner))
        }
        consumed = match.range.last + 1
    }
    if (consumed < text.length) out += Plain(text.substring(consumed))
    return out
}

private fun Style.wrap(content: List<Inline>): Inline = when (this) {
    Style.Bold -> cv.model.Bold(content)
    Style.Italic -> cv.model.Italic(content)
    Style.NoWrap -> cv.model.NoWrap(content)
    is Style.Colored -> cv.model.Colored(color, content)
    is Style.Link -> cv.model.Link(url, content)
}
