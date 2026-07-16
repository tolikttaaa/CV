package cv.render.web

import cv.model.SummarySection
import cv.render.ElementRenderer

/** Renders the summary together with the CV-level identity and contacts. */
internal object WebSummarySectionRenderer : ElementRenderer<SummarySection, WebRenderContext> {
    override fun render(element: SummarySection, context: WebRenderContext): String = with(element) {
        val cv = context.cv
        val fullName = "${cv.firstName} ${cv.lastName}"
        val contacts = cv.social.flatten().joinToString("") { WebRendererBundle.socialRenderer.render(it, context) }
        val photo = cv.photo?.let {
            """
                |<img class="summary-photo" src="${h(it.file)}" alt="${h(fullName)}"
                |     onerror="this.style.display='none'">
            """.trimMargin()
        }.orEmpty()

        """
            |<div class="summary-header">
            |  <div class="summary-header-name">${h(fullName)}</div>
            |  <div class="summary-header-tagline">${h(cv.tagline)}</div>
            |</div>
            |<div class="summary-grid">
            |  <div class="contact-list">$contacts</div>
            |  <div class="summary-box">${HtmlText.html(text)}</div>
            |  $photo
            |</div>
        """.trimMargin()
    }
}
