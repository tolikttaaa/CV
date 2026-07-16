package cv.render

import cv.model.EducationSection
import cv.model.ProjectsSection
import cv.model.ReferencesSection
import cv.model.Section
import cv.model.SkillsSection
import cv.model.SummarySection
import cv.model.WorksSection

/**
 * Typed visitor for the sealed [Section] hierarchy.
 *
 * [RendererBundle] supplies the default delegation to its section-level
 * [ElementRenderer] properties; this interface defines the exhaustive surface.
 */
internal interface SectionRenderer<C> {
    fun render(section: SummarySection, context: C): String
    fun render(section: WorksSection, context: C): String
    fun render(section: SkillsSection, context: C): String
    fun render(section: ProjectsSection, context: C): String
    fun render(section: EducationSection, context: C): String
    fun render(section: ReferencesSection, context: C): String
}

/**
 * Exhaustively dispatches this section to [renderer]. Adding a new [Section]
 * subtype fails compilation here until every format supports it.
 */
internal fun <C> Section.renderWith(renderer: SectionRenderer<C>, context: C): String = when (this) {
    is SummarySection -> renderer.render(this, context)
    is WorksSection -> renderer.render(this, context)
    is SkillsSection -> renderer.render(this, context)
    is ProjectsSection -> renderer.render(this, context)
    is EducationSection -> renderer.render(this, context)
    is ReferencesSection -> renderer.render(this, context)
}
