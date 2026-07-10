package cv.content

import cv.dsl.cv
import cv.model.Cv

/**
 * The single source of truth for the CV content.
 *
 * The header (name, tagline, contacts) lives here; each section body lives in
 * its own file in this package, mirroring the section order of the document:
 *
 *  - [summarySection]   — Summary.kt
 *  - [experienceSection] — Experience.kt
 *  - [skillsSection]    — Skills.kt
 *  - [personalProjectsSection] — PersonalProjects.kt
 *  - [teachingSection]  — Teaching.kt
 *  - [educationSection] — Education.kt
 *  - [referencesSection] — References.kt
 *
 * Body text (paragraphs, bullet items) is written as plain multiline strings —
 * indentation and line breaks collapse to single spaces, and special characters
 * are escaped automatically. Formatting is declared separately as highlight
 * rules matching a literal substring or a Regex:
 * ```
 * paragraph("…text mentioning Azul and ITMO University…") {
 *     bold("Azul")                                        – single style
 *     highlight("ITMO University", linkTo(url), bold)     – combined styles
 *     italic(Regex("[Cc]ontinuous \\w+"))                 – regex match
 * }
 * ```
 * Available styles: bold, italic, nowrap, colored(CvColor.X), linkTo(url).
 * A rule that matches nothing fails the build.
 */
val anatoliiCv: Cv = cv {
    firstName = "Anatolii"
    lastName = "Anishchenko"
    tagline = "Java/Kotlin Software Engineer"
    photo(file = "photo.jpg", size = "2.2cm")
    footerText = "Anatolii Anishchenko — CV"
    hyphenation = false // words always wrap whole; no per-word nowrap needed

    social {
        row {
            phone("+357 974 33 973")
            telegram("ttaaa_work")
            email("tolik.ttaaa@gmail.com")
        }
        row {
            linkedin("ttaaa")
            github("tolikttaaa")
            leetcode("ttaaa")
        }
        row {
            address("Cyprus, Limassol")
        }
    }

    summarySection()
    experienceSection()
    skillsSection()
    personalProjectsSection()
    teachingSection()
    educationSection()
    referencesSection()
}
