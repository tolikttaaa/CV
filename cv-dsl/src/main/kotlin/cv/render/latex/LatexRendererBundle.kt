package cv.render.latex

import cv.render.RendererBundle

/** Complete LaTeX renderer set; every required model renderer is declared here. */
internal object LatexRendererBundle : RendererBundle<Unit> {
    override val summarySectionRenderer = LatexSummarySectionRenderer
    override val worksSectionRenderer = LatexWorksSectionRenderer
    override val skillsSectionRenderer = LatexSkillsSectionRenderer
    override val projectsSectionRenderer = LatexProjectsSectionRenderer
    override val educationSectionRenderer = LatexEducationSectionRenderer
    override val referencesSectionRenderer = LatexReferencesSectionRenderer

    override val workRenderer = LatexWorkRenderer
    override val projectRenderer = LatexProjectRenderer
    override val skillEntryRenderer = LatexSkillEntryRenderer
    override val educationEntryRenderer = LatexEducationEntryRenderer
    override val refereeRenderer = LatexRefereeRenderer
    override val socialRenderer = LatexSocialRenderer
}
