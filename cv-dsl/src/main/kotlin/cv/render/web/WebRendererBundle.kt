package cv.render.web

import cv.render.RendererBundle

/** Complete web renderer set; every required model renderer is declared here. */
internal object WebRendererBundle : RendererBundle<WebRenderContext> {
    override val summarySectionRenderer = WebSummarySectionRenderer
    override val worksSectionRenderer = WebWorksSectionRenderer
    override val skillsSectionRenderer = WebSkillsSectionRenderer
    override val projectsSectionRenderer = WebProjectsSectionRenderer
    override val educationSectionRenderer = WebEducationSectionRenderer
    override val referencesSectionRenderer = WebReferencesSectionRenderer

    override val workRenderer = WebWorkRenderer
    override val projectRenderer = WebProjectRenderer
    override val skillEntryRenderer = WebSkillEntryRenderer
    override val educationEntryRenderer = WebEducationEntryRenderer
    override val refereeRenderer = WebRefereeRenderer
    override val socialRenderer = WebSocialRenderer
}
