package cv.model

/**
 * Named colors available for [Colored] text.
 *
 * Each color carries both its LaTeX name (must be defined in `documentMETADATA.cls`)
 * and a CSS equivalent used by the web renderer.
 */
enum class CvColor(val latexName: String, val css: String) {
    /** The primary theme color of the document. */
    BASE("basecolor", "#33127a"),

    /** A slightly lighter shade of the theme color, used for accents. */
    ACCENT("accentcolor", "#472b87"),

    /** Muted grey, used for secondary information. */
    DARK_GREY("darkGrey", "#989898"),
}

/**
 * A single node of inline (character-level) markup.
 *
 * Inline nodes form a tree: styling nodes wrap other inline content, so markup
 * can be nested (e.g. a bold word inside a link). Renderers translate each node
 * into their target format:
 *
 * | Node       | LaTeX          | HTML                     |
 * |------------|----------------|--------------------------|
 * | [Plain]    | escaped text   | escaped text             |
 * | [Bold]     | `\textbf`      | `<strong>`               |
 * | [Italic]   | `\emph`        | `<em>`                   |
 * | [Colored]  | `\textcolor`   | `<span style="color:…">` |
 * | [Link]     | `\link`        | `<a href="…">`           |
 * | [NoWrap]   | `\mbox`        | plain content            |
 */
sealed interface Inline

/** Plain text. Special characters are escaped by the renderers, not here. */
data class Plain(val text: String) : Inline

/** Bold emphasis. */
data class Bold(val content: List<Inline>) : Inline

/** Italic emphasis. */
data class Italic(val content: List<Inline>) : Inline

/** Text rendered in one of the theme colors. */
data class Colored(val color: CvColor, val content: List<Inline>) : Inline

/** A hyperlink wrapping arbitrary inline content. */
data class Link(val url: String, val content: List<Inline>) : Inline

/** Content that must not be broken across lines (LaTeX `\mbox`; no-op on the web). */
data class NoWrap(val content: List<Inline>) : Inline

/** A run of inline markup — one "rich string". */
data class RichText(val inlines: List<Inline>)

/**
 * A block-level element of a description: either a [Paragraph] or a [Bullets] list.
 */
sealed interface Block

/** A paragraph of rich text. */
data class Paragraph(val text: RichText) : Block

/** An unordered bullet list; each item is a run of rich text. */
data class Bullets(val items: List<RichText>) : Block

/** Multi-block body text, e.g. a job description: paragraphs mixed with bullet lists. */
data class Description(val blocks: List<Block>)
