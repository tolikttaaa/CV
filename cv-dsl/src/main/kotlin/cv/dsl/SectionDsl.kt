package cv.dsl

import cv.model.Organization
import cv.model.EducationEntry
import cv.model.Project
import cv.model.Referee
import cv.model.SkillEntry
import cv.model.Work

/**
 * Builder for the positions of a works section (experience, teaching, …).
 */
@CvDsl
class WorksBuilder {
    internal val works = mutableListOf<Work>()

    /**
     * Adds one position.
     *
     * @param role Job title.
     * @param company Employer; the name is always rendered in bold and linked
     *   when [Organization.url] is set.
     * @param location City / country, shown next to the company.
     * @param dates Human-readable period, e.g. `"May 2021 – January 2023"`.
     * @param tags Technology keywords shown as chips under the description.
     * @param description Body text: paragraphs and bullet lists.
     */
    fun work(
        role: String,
        company: Organization,
        location: String,
        dates: String,
        tags: List<String>,
        description: DescriptionBuilder.() -> Unit,
    ) {
        works += Work(
            role = role,
            company = company,
            location = location,
            dates = dates,
            description = DescriptionBuilder().apply(description).build(),
            tags = tags,
        )
    }
}

/**
 * Builder for the rows of the skills table.
 */
@CvDsl
class SkillsBuilder {
    internal val entries = mutableListOf<SkillEntry>()

    /** Adds a row: a [category] and the [skills] belonging to it. */
    fun entry(category: String, skills: List<String>) {
        entries += SkillEntry(category, skills)
    }
}

/**
 * Builder for the entries of a personal-projects section.
 */
@CvDsl
class ProjectsBuilder {
    internal val projects = mutableListOf<Project>()

    /**
     * Adds one project.
     *
     * @param name Project name.
     * @param company Organization the project was built at; the name is always
     *   rendered in bold and linked when [Organization.url] is set.
     * @param location City / country, shown after the company; omit if not tied to a place.
     * @param year Year or period of the project.
     * @param tags Technology keywords shown as chips under the description.
     * @param description Body text: paragraphs and bullet lists.
     */
    fun project(
        name: String,
        company: Organization,
        location: String? = null,
        year: String,
        tags: List<String>,
        description: DescriptionBuilder.() -> Unit,
    ) {
        projects += Project(
            name = name,
            company = company,
            location = location,
            year = year,
            description = DescriptionBuilder().apply(description).build(),
            tags = tags,
        )
    }
}

/**
 * Builder for the milestones of an education section.
 */
@CvDsl
class EducationBuilder {
    internal val entries = mutableListOf<EducationEntry>()

    /** Adds a milestone: a [years] label and a free-form rich-text [description]. */
    fun entry(years: String, description: TextBuilder.() -> Unit) {
        entries += EducationEntry(years, richText(description))
    }

    /**
     * Adds a milestone assembled from structured fields, rendered as
     * `"<degree>: <institution>, <location>"`. The institution name is
     * emphasized in bold and linked when [Organization.url] is set.
     *
     * @param years Period label, e.g. `"2018 – 2022"`.
     * @param degree What was obtained, e.g. `"Bachelor’s Degree in Software Engineering"`.
     * @param institution School or university.
     * @param location City / country of the institution.
     */
    fun entry(
        years: String,
        degree: String,
        institution: Organization,
        location: String,
    ) {
        entries += EducationEntry(years, richText {
            +"$degree: "
            val url = institution.url
            if (url != null) link(url) { bold(institution.name) } else bold(institution.name)
            +", $location"
        })
    }
}

/**
 * Builder for the entries of a references section.
 */
@CvDsl
class ReferencesBuilder {
    internal val referees = mutableListOf<Referee>()

    /**
     * Adds one professional reference. Only the [company] name is shown in this
     * section — the compact referee block stays unstyled and unlinked.
     */
    fun referee(name: String, role: String, company: Organization, period: String, email: String) {
        referees += Referee(name, role, company, period, email)
    }
}
