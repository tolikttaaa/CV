package cv.dsl

import cv.model.Cv
import cv.model.EducationSection
import cv.model.ProjectsSection
import cv.model.ReferencesSection
import cv.model.Section
import cv.model.SkillsSection
import cv.model.Social
import cv.model.SummarySection
import cv.model.WorksSection

/**
 * Root builder of the CV DSL.
 *
 * Header fields are set as properties; the contact block via [social];
 * sections are appended in call order by the section methods
 * ([summary], [experience], [skills], [projects], [education], [references]).
 *
 * Every section method takes:
 *  - `title` — the heading in the PDF,
 *  - `icon` — the FontAwesome command for `\sectionTitle` (e.g. `"faSuitcase"`),
 *  - `id` — the LaTeX file / JSON section identifier (defaulted where unambiguous),
 *  - `webTitle` — the heading on the web page, when it differs from `title`.
 */
@CvDsl
class CvBuilder {
    /** Given name in display case (uppercased for the PDF header by the LaTeX renderer). */
    var firstName: String = ""

    /** Family name in display case. */
    var lastName: String = ""

    /** Short professional title shown under the name. */
    var tagline: String = ""

    /** File name of the profile photo, relative to the LaTeX build directory. */
    var photo: String = "photo.jpg"

    /** Photo diameter as a LaTeX length. */
    var photoSize: String = "2.2cm"

    /** Text placed in the page footer of the PDF. */
    var footerText: String = ""

    /**
     * Whether the PDF may hyphenate words across lines. Set to `false` to keep
     * every word whole document-wide instead of marking individual words with
     * [TextBuilder.nowrap].
     */
    var hyphenation: Boolean = true

    private var social: List<List<Social>> = emptyList()
    private val sections = mutableListOf<Section>()

    /** Defines the contact block of the header as rows of entries. */
    fun social(block: SocialBuilder.() -> Unit) {
        social = SocialBuilder().apply(block).rows.toList()
    }

    /** Adds a summary (free-form introduction) section. */
    fun summary(
        title: String,
        icon: String,
        id: String = "summary",
        webTitle: String = title,
        block: DescriptionBuilder.() -> Unit,
    ) {
        sections += SummarySection(id, title, webTitle, icon, DescriptionBuilder().apply(block).build())
    }

    /** Adds a section listing positions — used for both work experience and teaching. */
    fun experience(
        title: String,
        icon: String,
        id: String,
        webTitle: String = title,
        block: WorksBuilder.() -> Unit,
    ) {
        sections += WorksSection(id, title, webTitle, icon, WorksBuilder().apply(block).works.toList())
    }

    /** Adds a skills table section. */
    fun skills(
        title: String,
        icon: String,
        id: String = "skills",
        webTitle: String = title,
        block: SkillsBuilder.() -> Unit,
    ) {
        sections += SkillsSection(id, title, webTitle, icon, SkillsBuilder().apply(block).entries.toList())
    }

    /** Adds a personal-projects section. */
    fun projects(
        title: String,
        icon: String,
        id: String = "projects",
        webTitle: String = title,
        block: ProjectsBuilder.() -> Unit,
    ) {
        sections += ProjectsSection(id, title, webTitle, icon, ProjectsBuilder().apply(block).projects.toList())
    }

    /** Adds an education timeline section. */
    fun education(
        title: String,
        icon: String,
        id: String = "education",
        webTitle: String = title,
        block: EducationBuilder.() -> Unit,
    ) {
        sections += EducationSection(id, title, webTitle, icon, EducationBuilder().apply(block).entries.toList())
    }

    /** Adds a references section. */
    fun references(
        title: String,
        icon: String,
        id: String = "references",
        webTitle: String = title,
        block: ReferencesBuilder.() -> Unit,
    ) {
        sections += ReferencesSection(id, title, webTitle, icon, ReferencesBuilder().apply(block).referees.toList())
    }

    /** Finalizes the builder into an immutable [Cv]. */
    fun build() = Cv(
        firstName = firstName,
        lastName = lastName,
        tagline = tagline,
        photo = photo,
        photoSize = photoSize,
        footerText = footerText,
        hyphenation = hyphenation,
        social = social,
        sections = sections.toList(),
    )
}

/** Entry point of the CV DSL: builds a complete [Cv] document. */
fun cv(block: CvBuilder.() -> Unit): Cv = CvBuilder().apply(block).build()
