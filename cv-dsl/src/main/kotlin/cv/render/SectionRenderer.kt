package cv.render

import cv.model.EducationSection
import cv.model.ProjectsSection
import cv.model.ReferencesSection
import cv.model.Section
import cv.model.SkillsSection
import cv.model.SummarySection
import cv.model.WorksSection

/** Typed visitor implemented once per output representation. */
internal interface SectionRenderer<C> {
    fun render(section: SummarySection, context: C): String
    fun render(section: WorksSection, context: C): String
    fun render(section: SkillsSection, context: C): String
    fun render(section: ProjectsSection, context: C): String
    fun render(section: EducationSection, context: C): String
    fun render(section: ReferencesSection, context: C): String
}

/** Exhaustive shared dispatch for the sealed section hierarchy. */
internal fun <C> Section.renderWith(renderer: SectionRenderer<C>, context: C): String = when (this) {
    is SummarySection -> renderer.render(this, context)
    is WorksSection -> renderer.render(this, context)
    is SkillsSection -> renderer.render(this, context)
    is ProjectsSection -> renderer.render(this, context)
    is EducationSection -> renderer.render(this, context)
    is ReferencesSection -> renderer.render(this, context)
}
