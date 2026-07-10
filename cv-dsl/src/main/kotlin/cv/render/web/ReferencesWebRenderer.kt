package cv.render.web

import cv.model.Referee
import cv.model.ReferencesSection
import cv.render.ElementRenderer

/** Renders the references collection and individual referee cards. */
internal object WebReferencesSectionRenderer : ElementRenderer<ReferencesSection, WebRenderContext> {
    override fun render(element: ReferencesSection, context: WebRenderContext): String = with(element) {
        renderTitle() +
            "<div class=\"ref-grid\">${referees.joinToString("") { WebRendererBundle.refereeRenderer.render(it, context) }}</div>"
    }
}

internal object WebRefereeRenderer : ElementRenderer<Referee, WebRenderContext> {
    override fun render(element: Referee, context: WebRenderContext): String = with(element) {
        """
            |<div class="ref-card">
            |  <div class="ref-name">${h(name)}</div>
            |  <div class="ref-detail">
            |    <div>${h(role)}</div>
            |    <div>${company.renderWeb(emphasized = false)} · ${h(period)}</div>
            |    <div><a href="mailto:${h(email)}">${h(email)}</a></div>
            |  </div>
            |</div>
        """.trimMargin()
    }
}
