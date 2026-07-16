package cv.render.web

import cv.model.Project
import cv.model.ProjectsSection
import cv.render.ElementRenderer

/** Renders the projects' collection and individual project cards. */
internal object WebProjectsSectionRenderer : ElementRenderer<ProjectsSection, WebRenderContext> {
    override fun render(element: ProjectsSection, context: WebRenderContext): String = with(element) {
        renderTitle() +
            "<div class=\"project-entries\">${projects.joinToString("") { WebRendererBundle.projectRenderer.render(it, context) }}</div>"
    }
}

internal object WebProjectRenderer : ElementRenderer<Project, WebRenderContext> {
    override fun render(element: Project, context: WebRenderContext): String = with(element) {
        """
            |<div class="project-card">
            |  <div class="project-header">
            |    <div class="project-name">${h(name)}</div>
            |    <div class="work-dates">${h(dates)}</div>
            |  </div>
            |  <div class="project-sub">${company.renderWeb(emphasized = true)}</div>
            |  <div class="work-desc">${HtmlText.html(description)}</div>
            |  ${tags.renderWeb()}
            |</div>
        """.trimMargin()
    }
}
