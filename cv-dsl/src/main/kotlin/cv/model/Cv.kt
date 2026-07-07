package cv.model

/**
 * The complete, immutable CV document — the root of the model tree.
 *
 * Instances are produced by the `cv { … }` DSL (see `cv.dsl.CvBuilder`) and
 * consumed by the renderers in `cv.render`. The model is renderer-agnostic:
 * it carries no LaTeX or HTML specifics beyond the [Section.icon] names.
 *
 * @property firstName Given name in display case (the LaTeX renderer uppercases it for the header).
 * @property lastName Family name in display case.
 * @property tagline Short professional title shown under the name.
 * @property photo File name of the profile photo, relative to the LaTeX build directory.
 * @property photoSize Photo diameter as a LaTeX length, e.g. `"2.2cm"`.
 * @property footerText Text placed in the page footer of the PDF.
 * @property hyphenation Whether the PDF may hyphenate words across lines.
 *   When `false`, words always move to the next line whole — a document-wide
 *   alternative to marking individual words with `nowrap`. Ignored on the web,
 *   where browsers do not hyphenate by default.
 * @property social Contact entries of the header, grouped into visual rows.
 * @property sections CV sections in display order.
 */
data class Cv(
    val firstName: String,
    val lastName: String,
    val tagline: String,
    val photo: String,
    val photoSize: String,
    val footerText: String,
    val hyphenation: Boolean,
    val social: List<List<Social>>,
    val sections: List<Section>,
)
