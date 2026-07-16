package cv.render.web

import cv.model.Work
import cv.model.WorksSection
import cv.render.ElementRenderer

/** Renders employment and teaching sections, which share the works' layout. */
internal object WebWorksSectionRenderer : ElementRenderer<WorksSection, WebRenderContext> {
    override fun render(element: WorksSection, context: WebRenderContext): String = with(element) {
        renderTitle() +
            "<div class=\"work-entries\">${works.joinToString("") { WebRendererBundle.workRenderer.render(it, context) }}</div>"
    }
}

internal object WebWorkRenderer : ElementRenderer<Work, WebRenderContext> {
    override fun render(element: Work, context: WebRenderContext): String = with(element) {
        """
            |<div class="work-card">
            |  <div class="work-header">
            |    <div>
            |      <div class="work-role">${h(role)}</div>
            |      <div class="work-sub">${company.renderWeb(emphasized = true)} &middot; ${h(location)}</div>
            |    </div>
            |    <div class="work-dates">${h(dates)}</div>
            |  </div>
            |  <div class="work-desc">${HtmlText.html(description)}</div>
            |  ${tags.renderWeb()}
            |</div>
        """.trimMargin()
    }
}
