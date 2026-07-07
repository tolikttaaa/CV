package cv.render.latex

import cv.model.Bullets
import cv.model.Organization
import cv.model.Cv
import cv.model.Description
import cv.model.EducationSection
import cv.model.Paragraph
import cv.model.Project
import cv.model.ProjectsSection
import cv.model.ReferencesSection
import cv.model.Section
import cv.model.SkillsSection
import cv.model.Social
import cv.model.SummarySection
import cv.model.Work
import cv.model.WorksSection
import cv.render.latex.LatexText.escape
import java.nio.file.Files
import java.nio.file.Path

/**
 * Renders the CV model to a compilable LaTeX source directory: a root `cv.tex`,
 * one `sections/<id>.tex` file per section, plus the bundled [LatexTemplate]
 * (document class and fonts). The only file the caller must add is the
 * profile photo referenced by [Cv.photo].
 */
object LatexRenderer {

    /** Writes `cv.tex`, all section files, and the bundled template into [outDir]. */
    fun render(cv: Cv, outDir: Path) {
        Files.createDirectories(outDir.resolve("sections"))
        LatexTemplate.extractTo(outDir)
        outDir.resolve("cv.tex").toFile().writeText(rootDocument(cv))
        for (section in cv.sections) {
            outDir.resolve("sections/${section.id}.tex").toFile()
                .writeText(renderSection(section))
        }
    }

    // ── Root document ─────────────────────────────────────────────────────────

    private fun rootDocument(cv: Cv): String = buildString {
        appendLine("""\documentclass[localFont,alternative]{cvdsl}""")
        if (!cv.hyphenation) {
            // Forbid hyphenation document-wide; emergencystretch lets justified
            // lines breathe instead of overflowing once words must stay whole.
            appendLine("""\hyphenpenalty=10000""")
            appendLine("""\exhyphenpenalty=10000""")
            appendLine("""\emergencystretch=3em""")
        }
        appendLine("""\name{${escape(cv.firstName.uppercase())}}{${escape(cv.lastName.uppercase())}}""")
        appendLine("""\tagline{${escape(cv.tagline)}}""")
        appendLine()
        appendLine("""\socialinfo{""")
        append(cv.social.joinToString(" \\\\\n") { row ->
            "    " + row.joinToString(" ") { social(it) }
        })
        appendLine()
        appendLine("""}""")
        appendLine()
        cv.photo?.let { photo ->
            appendLine("""\photo{${photo.size}}{${photo.file}}""")
            appendLine()
        }
        appendLine("""\begin{document}""")
        appendLine("""    \makecvheader""")
        appendLine("""    \makecvfooter{}{\textsc{${escape(cv.footerText)}}}{\thepage}""")
        appendLine()
        for (section in cv.sections) {
            appendLine("""    \input{sections/${section.id}}""")
        }
        appendLine("""\end{document}""")
    }

    private fun social(s: Social): String = when (s) {
        is Social.Phone -> """\smartphone{${escape(s.number)}}"""
        is Social.Telegram -> """\telegram{${escape(s.handle)}}"""
        is Social.Email -> """\email{${escape(s.address)}}"""
        is Social.LinkedIn -> """\linkedin{${escape(s.handle)}}"""
        is Social.LeetCode -> """\leetcode{${escape(s.handle)}}"""
        is Social.GitHub -> """\github{${escape(s.handle)}}"""
        is Social.Address -> """\address{${escape(s.text)}}"""
    }

    // ── Sections ──────────────────────────────────────────────────────────────

    private fun renderSection(section: Section): String = buildString {
        appendLine("""\sectionTitle{${escape(section.title)}}{\${section.icon}}""")
        when (section) {
            is SummarySection -> {
                appendLine("""\begin{summary}""")
                appendLine("""    \summaryText{""")
                append(blocks(section.text, indent = 2))
                appendLine("""    }""")
                appendLine("""\end{summary}""")
            }
            is WorksSection -> {
                if (section.works.any { w -> w.description.blocks.any { it is Bullets } }) {
                    appendLine("""\renewcommand{\labelitemi}{${'$'}\bullet${'$'}}""")
                }
                appendLine("""\begin{works}""")
                for (work in section.works) append(work(work))
                appendLine("""\end{works}""")
            }
            is SkillsSection -> {
                appendLine("""\begin{keywords}""")
                for (e in section.entries) {
                    appendLine("""    \keywordsentry{${escape(e.category)}}{${escape(e.skills.joinToString(", "))}}""")
                }
                appendLine("""\end{keywords}""")
            }
            is ProjectsSection -> {
                appendLine("""\begin{projects}""")
                for (p in section.projects) append(project(p))
                appendLine("""\end{projects}""")
            }
            is EducationSection -> {
                appendLine("""\begin{education}""")
                for (e in section.entries) {
                    appendLine("""    \educationentry{${escape(e.years)}}{${LatexText.render(e.description)}}""")
                }
                appendLine("""\end{education}""")
            }
            is ReferencesSection -> {
                appendLine("""\begin{referees}""")
                for (r in section.referees) {
                    appendLine("""    \referee""")
                    appendLine("""        {${escape(r.name)}}""")
                    appendLine("""        {${escape(r.role)}}""")
                    appendLine("""        {${refereeCompany(r.company)}} {${escape(r.period)}}""")
                    appendLine("""        {${escape(r.email)}}""")
                }
                appendLine("""\end{referees}""")
            }
        }
    }

    private fun work(w: Work): String = buildString {
        appendLine("""    \work""")
        appendLine("""        {${escape(w.role)}} {${company(w.company)}} {${escape(w.location)}} {${escape(w.dates)}}""")
        appendLine("""        {""")
        append(blocks(w.description, indent = 3))
        appendLine("""        }""")
        appendLine("""        {${escape(w.tags.joinToString(", "))}}""")
    }

    private fun project(p: Project): String = buildString {
        appendLine("""    \project""")
        appendLine("""        {${escape(p.name)}}""")
        appendLine("""        {${company(p.company)}}""")
        appendLine("""        {${escape(p.dates)}}""")
        appendLine("""        {""")
        append(blocks(p.description, indent = 3))
        appendLine("""        }""")
        appendLine("""        {${escape(p.tags.joinToString(", "))}}""")
    }

    /** Renders a company: name always bold, linked when a URL is present. */
    private fun company(c: Organization): String {
        val name = """\textbf{${escape(c.name)}}"""
        return if (c.url != null) """\link{${LatexText.escapeUrl(c.url)}}{$name}""" else name
    }

    /** Renders a referee's company: linked when a URL is present, but not bold — the referee block stays compact. */
    private fun refereeCompany(c: Organization): String =
        if (c.url != null) """\link{${LatexText.escapeUrl(c.url)}}{${escape(c.name)}}""" else escape(c.name)

    // ── Block content ─────────────────────────────────────────────────────────

    private fun blocks(description: Description, indent: Int): String = buildString {
        val pad = "    ".repeat(indent)
        for ((i, block) in description.blocks.withIndex()) {
            if (i > 0) appendLine()
            when (block) {
                is Paragraph -> appendLine(pad + LatexText.render(block.text))
                is Bullets -> {
                    appendLine("""$pad\begin{itemize}""")
                    for (item in block.items) {
                        appendLine("""$pad    \item {${LatexText.render(item)}}""")
                    }
                    appendLine("""$pad\end{itemize}""")
                }
            }
        }
    }
}
