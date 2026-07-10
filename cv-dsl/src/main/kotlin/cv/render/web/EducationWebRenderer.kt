package cv.render.web

import cv.model.EducationEntry
import cv.model.EducationSection
import cv.render.ElementRenderer

/** Renders the education timeline and its individual entries. */
internal object WebEducationSectionRenderer : ElementRenderer<EducationSection, WebRenderContext> {
    override fun render(element: EducationSection, context: WebRenderContext): String = with(element) {
        renderTitle() +
            "<div class=\"edu-entries\">${entries.joinToString("") { WebRendererBundle.educationEntryRenderer.render(it, context) }}</div>"
    }
}

internal object WebEducationEntryRenderer : ElementRenderer<EducationEntry, WebRenderContext> {
    override fun render(element: EducationEntry, context: WebRenderContext): String = with(element) {
        """
            |<div class="edu-entry">
            |  <div class="edu-years">${h(years)}</div>
            |  <div class="edu-desc">${HtmlText.html(description)}</div>
            |</div>
        """.trimMargin()
    }
}
