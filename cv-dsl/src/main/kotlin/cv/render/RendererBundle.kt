package cv.render

import cv.model.EducationEntry
import cv.model.EducationSection
import cv.model.Project
import cv.model.ProjectsSection
import cv.model.Referee
import cv.model.ReferencesSection
import cv.model.SkillEntry
import cv.model.SkillsSection
import cv.model.Social
import cv.model.SummarySection
import cv.model.Work
import cv.model.WorksSection

/**
 * Complete set of renderers required by one output representation.
 *
 * Adding a new supported model type here makes both the web and LaTeX bundles
 * fail compilation until they provide an implementation.
 */
internal interface RendererBundle<C> : SectionRenderer<C> {
    val summarySectionRenderer: ElementRenderer<SummarySection, C>
    val worksSectionRenderer: ElementRenderer<WorksSection, C>
    val skillsSectionRenderer: ElementRenderer<SkillsSection, C>
    val projectsSectionRenderer: ElementRenderer<ProjectsSection, C>
    val educationSectionRenderer: ElementRenderer<EducationSection, C>
    val referencesSectionRenderer: ElementRenderer<ReferencesSection, C>

    val workRenderer: ElementRenderer<Work, C>
    val projectRenderer: ElementRenderer<Project, C>
    val skillEntryRenderer: ElementRenderer<SkillEntry, C>
    val educationEntryRenderer: ElementRenderer<EducationEntry, C>
    val refereeRenderer: ElementRenderer<Referee, C>
    val socialRenderer: ElementRenderer<Social, C>

    override fun render(section: SummarySection, context: C): String =
        summarySectionRenderer.render(section, context)

    override fun render(section: WorksSection, context: C): String =
        worksSectionRenderer.render(section, context)

    override fun render(section: SkillsSection, context: C): String =
        skillsSectionRenderer.render(section, context)

    override fun render(section: ProjectsSection, context: C): String =
        projectsSectionRenderer.render(section, context)

    override fun render(section: EducationSection, context: C): String =
        educationSectionRenderer.render(section, context)

    override fun render(section: ReferencesSection, context: C): String =
        referencesSectionRenderer.render(section, context)
}
