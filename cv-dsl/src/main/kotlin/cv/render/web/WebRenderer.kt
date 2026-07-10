package cv.render.web

import cv.model.Organization
import cv.model.Cv
import cv.model.EducationSection
import cv.model.ProjectsSection
import cv.model.ReferencesSection
import cv.model.Section
import cv.model.SkillsSection
import cv.model.Social
import cv.model.SummarySection
import cv.model.WorksSection
import java.nio.file.Files
import java.nio.file.Path

/**
 * Renders the CV model to the data file consumed by the web portfolio view
 * (`web/portfolio.js`): a JSON document whose text fields are pre-rendered
 * HTML fragments (see [HtmlText]).
 *
 * The JSON schema is the contract between the generator and the static web
 * app — if a field is renamed here, `web/portfolio.js` must follow.
 */
object WebRenderer {

    /** Writes the portfolio data document to [outFile], creating parent directories. */
    fun render(cv: Cv, outFile: Path) {
        val root = Json.Obj(
            listOfNotNull(
                "name" to obj(
                    "first" to str(cv.firstName),
                    "last" to str(cv.lastName),
                ),
                "tagline" to str(cv.tagline),
                cv.photo?.let { "photo" to str(it.file) },
                "social" to arr(cv.social.flatten().map { social(it) }),
                "sections" to arr(cv.sections.map { section(it) }),
            ),
        )
        Files.createDirectories(outFile.parent)
        outFile.toFile().writeText(JsonWriter.write(root))
    }

    // ── Social ────────────────────────────────────────────────────────────────

    /** Maps a contact entry to its web representation: icon, display text and URL. */
    private fun social(s: Social): Json = when (s) {
        is Social.Phone -> obj(
            "type" to str("phone"), "icon" to str("phone"),
            "text" to str(s.number),
        )
        is Social.Telegram -> obj(
            "type" to str("telegram"), "icon" to str("paper-plane"),
            "url" to str("https://t.me/${s.handle}"), "text" to str("t.me/${s.handle}"),
        )
        is Social.Email -> obj(
            "type" to str("email"), "icon" to str("envelope"),
            "url" to str("mailto:${s.address}"), "text" to str(s.address),
        )
        is Social.LinkedIn -> obj(
            "type" to str("linkedin"), "icon" to str("linkedin"),
            "url" to str("https://www.linkedin.com/in/${s.handle}"),
            "text" to str("linkedin.com/in/${s.handle}"),
        )
        is Social.LeetCode -> obj(
            "type" to str("leetcode"), "icon" to str("code"),
            "url" to str("https://leetcode.com/u/${s.handle}"),
            "text" to str("leetcode.com/u/${s.handle}"),
        )
        is Social.GitHub -> obj(
            "type" to str("github"), "icon" to str("github"),
            "url" to str("https://github.com/${s.handle}"),
            "text" to str("github.com/${s.handle}"),
        )
        is Social.Address -> obj(
            "type" to str("address"), "icon" to str("location-dot"),
            "text" to str(s.text),
        )
    }

    /** Renders a referee's company: linked when a URL is present, but not bold — the referee card stays compact. */
    private fun refereeCompanyHtml(c: Organization): String =
        if (c.url != null) {
            """<a href="${HtmlText.escape(c.url)}" target="_blank" rel="noopener">${HtmlText.escape(c.name)}</a>"""
        } else {
            HtmlText.escape(c.name)
        }

    /** Renders a company: name always bold, linked when a URL is present. */
    private fun companyHtml(c: Organization): String {
        val name = "<strong>${HtmlText.escape(c.name)}</strong>"
        return if (c.url != null) {
            """<a href="${HtmlText.escape(c.url)}" target="_blank" rel="noopener">$name</a>"""
        } else {
            name
        }
    }

    // ── Sections ──────────────────────────────────────────────────────────────

    private fun section(section: Section): Json {
        val head = listOf(
            "id" to str(section.id),
            "title" to str(HtmlText.escape(section.webTitle)),
        )
        return when (section) {
            is SummarySection -> Json.Obj(
                head + listOf(
                    "type" to str("summary"),
                    "html" to str(HtmlText.html(section.text)),
                )
            )
            is WorksSection -> Json.Obj(
                head + listOf(
                    "type" to str("works"),
                    "items" to arr(section.works.map { w ->
                        obj(
                            "role" to str(w.role),
                            "company_html" to str(companyHtml(w.company)),
                            "location" to str(w.location),
                            "dates" to str(w.dates),
                            "description_html" to str(HtmlText.html(w.description)),
                            "tags" to arr(w.tags.map { str(it) }),
                        )
                    }),
                )
            )
            is SkillsSection -> Json.Obj(
                head + listOf(
                    "type" to str("skills"),
                    "items" to arr(section.entries.map { e ->
                        obj(
                            "category" to str(e.category),
                            "values" to str(e.skills.joinToString(", ")),
                        )
                    }),
                )
            )
            is ProjectsSection -> Json.Obj(
                head + listOf(
                    "type" to str("projects"),
                    "items" to arr(section.projects.map { p ->
                        obj(
                            "name" to str(p.name),
                            "company_html" to str(companyHtml(p.company)),
                            "dates" to str(p.dates),
                            "description_html" to str(HtmlText.html(p.description)),
                            "tags" to arr(p.tags.map { str(it) }),
                        )
                    }),
                )
            )
            is EducationSection -> Json.Obj(
                head + listOf(
                    "type" to str("education"),
                    "items" to arr(section.entries.map { e ->
                        obj(
                            "years" to str(e.years),
                            "description_html" to str(HtmlText.html(e.description)),
                        )
                    }),
                )
            )
            is ReferencesSection -> Json.Obj(
                head + listOf(
                    "type" to str("references"),
                    "items" to arr(section.referees.map { r ->
                        obj(
                            "name" to str(r.name),
                            "role" to str(r.role),
                            "company_html" to str(refereeCompanyHtml(r.company)),
                            "period" to str(r.period),
                            "email" to str(r.email),
                        )
                    }),
                )
            )
        }
    }
}
