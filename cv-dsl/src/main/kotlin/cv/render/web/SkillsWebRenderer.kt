package cv.render.web

import cv.model.SkillEntry
import cv.model.SkillsSection
import cv.render.ElementRenderer

/** Renders the skills table and each category row. */
internal object WebSkillsSectionRenderer : ElementRenderer<SkillsSection, WebRenderContext> {
    override fun render(element: SkillsSection, context: WebRenderContext): String = with(element) {
        renderTitle() +
            "<div class=\"skills-table\">${entries.joinToString("") { WebRendererBundle.skillEntryRenderer.render(it, context) }}</div>"
    }
}

internal object WebSkillEntryRenderer : ElementRenderer<SkillEntry, WebRenderContext> {
    override fun render(element: SkillEntry, context: WebRenderContext): String = with(element) {
        """
            |<div class="skill-row">
            |  <div class="skill-cat">${h(category)}</div>
            |  <div class="skill-val">${h(skills.joinToString(", "))}</div>
            |</div>
        """.trimMargin()
    }
}
