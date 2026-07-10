package cv.model

/**
 * A titled section of the CV.
 *
 * There is one implementation per section layout; the renderers dispatch on the
 * concrete type to produce the matching LaTeX environment or JSON section type.
 */
sealed interface Section {
    /** Stable identifier: the generated LaTeX file name (`sections/<id>.tex`) and the JSON section id. */
    val id: String

    /** Section heading in the PDF. */
    val title: String

    /** Section heading on the web page (defaults to [title] when built through the DSL). */
    val webTitle: String

    /** FontAwesome icon command used by `\sectionTitle`, e.g. `"faSuitcase"`. */
    val icon: String
}

/** Free-form introduction text at the top of the CV. */
data class SummarySection(
    override val id: String,
    override val title: String,
    override val webTitle: String,
    override val icon: String,
    val text: Description,
) : Section

/**
 * An organization a position or project belongs to. Renderers always emphasize
 * the [name] in bold and wrap it in a hyperlink when [url] is present.
 */
data class Organization(val name: String, val url: String? = null)

/** One employment (or teaching) position inside a [WorksSection]. */
data class Work(
    val role: String,
    val company: Organization,
    val location: String,
    val dates: String,
    val description: Description,
    val tags: List<String>,
)

/** A chronological list of positions — used for both work experience and teaching. */
data class WorksSection(
    override val id: String,
    override val title: String,
    override val webTitle: String,
    override val icon: String,
    val works: List<Work>,
) : Section

/** One row of the skills table: a category and the skills belonging to it. */
data class SkillEntry(val category: String, val skills: List<String>)

/** A two-column keyword table of skill categories. */
data class SkillsSection(
    override val id: String,
    override val title: String,
    override val webTitle: String,
    override val icon: String,
    val entries: List<SkillEntry>,
) : Section

/** One personal project inside a [ProjectsSection]. */
data class Project(
    val name: String,
    val company: Organization,
    val dates: String,
    val description: Description,
    val tags: List<String>,
)

/** A list of personal / side projects. */
data class ProjectsSection(
    override val id: String,
    override val title: String,
    override val webTitle: String,
    override val icon: String,
    val projects: List<Project>,
) : Section

/** One education milestone: a year range and its rich-text description. */
data class EducationEntry(val years: String, val description: RichText)

/** A timeline of education milestones. */
data class EducationSection(
    override val id: String,
    override val title: String,
    override val webTitle: String,
    override val icon: String,
    val entries: List<EducationEntry>,
) : Section

/** A single professional reference. */
data class Referee(
    val name: String,
    val role: String,
    val company: Organization,
    val period: String,
    val email: String,
)

/** A list of professional references with contact details. */
data class ReferencesSection(
    override val id: String,
    override val title: String,
    override val webTitle: String,
    override val icon: String,
    val referees: List<Referee>,
) : Section
