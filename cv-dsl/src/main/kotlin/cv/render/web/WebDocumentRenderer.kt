package cv.render.web

import cv.model.Cv
import cv.render.renderWith

/** Renders the shared page chrome around the type-specific CV structures. */
@Suppress("LongMethod") // Keeping the static page shell together makes its HTML hierarchy readable.
internal fun Cv.renderWebDocument(): String {
    val fullName = "$firstName $lastName"
    val context = WebRenderContext(this)
    val navigation = sections.mapIndexed { index, section ->
        val active = if (index == 0) " active" else ""
        """
            |<div class="nav-item$active" data-index="$index" role="button" tabindex="0">
            |  <i class="fa-solid fa-${section.webIcon()}"></i>${h(section.webTitle)}
            |</div>
        """.trimMargin()
    }.joinToString("\n")
    val sectionMarkup = sections.mapIndexed { index, section ->
        val hidden = if (index == 0) "" else " hidden"
        """
            |<section class="portfolio-section" data-title="${h(section.webTitle)}"$hidden>
            |  ${section.renderWith(WebRendererBundle, context)}
            |</section>
        """.trimMargin()
    }.joinToString("\n")

    return """
        |<!DOCTYPE html>
        |<html lang="en">
        |<head>
        |  <meta charset="UTF-8">
        |  <meta name="viewport" content="width=device-width, initial-scale=1.0">
        |  <title>${h(fullName)} — Portfolio</title>
        |  <link rel="icon" type="image/png" href="favicon.png">
        |  <link rel="preconnect" href="https://fonts.googleapis.com">
        |  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        |  <link href="https://fonts.googleapis.com/css2?family=Source+Sans+3:ital,wght@0,300;0,400;0,600;0,700;1,300;1,400&amp;display=swap" rel="stylesheet">
        |  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        |  <link rel="stylesheet" href="styles.css">
        |</head>
        |<body>
        |  <div id="sidebar-overlay"></div>
        |
        |  <header id="app-header">
        |    <div class="h-left">
        |      <button class="menu-btn" id="menu-btn" type="button" title="Sections">
        |        <i class="fa-solid fa-bars"></i>
        |      </button>
        |      <div class="h-identity">
        |        <h1 id="h-name">${h(fullName)}</h1>
        |        <p id="h-tagline">${h(tagline)}</p>
        |      </div>
        |    </div>
        |    <div class="view-toggle">
        |      <button class="toggle-btn" type="button" data-mode="portfolio">Portfolio</button>
        |      <button class="toggle-btn" type="button" data-mode="pdf">CV</button>
        |    </div>
        |    <div class="h-actions">
        |      <span id="zoom-controls" style="display:none;align-items:center;gap:.35rem">
        |        <button class="zoom-btn" id="zoom-out" type="button" title="Zoom out">−</button>
        |        <span class="zoom-label" id="zoom-label">100%</span>
        |        <button class="zoom-btn" id="zoom-in" type="button" title="Zoom in">+</button>
        |      </span>
        |      <a class="download-btn" href="cv.pdf" download="CV.pdf">
        |        <i class="fa-solid fa-download"></i><span class="btn-label">PDF</span>
        |      </a>
        |    </div>
        |  </header>
        |
        |  <div id="pdf-view" class="view-panel hidden">
        |    <div id="viewer-container">
        |      <span id="pdf-loading">Loading…</span>
        |    </div>
        |  </div>
        |
        |  <div id="portfolio-view" class="view-panel hidden">
        |    <aside id="sidebar">
        |      <nav class="sb-nav">
        |        <div class="sb-nav-label">Sections</div>
        |        $navigation
        |      </nav>
        |    </aside>
        |    <div id="content-area">
        |      <div id="section-display">
        |        $sectionMarkup
        |      </div>
        |      <div id="section-nav-bar">
        |        <button class="nav-btn" id="prev-btn" type="button">
        |          <i class="fa-solid fa-chevron-left"></i> Previous
        |        </button>
        |        <span class="nav-indicator" id="nav-indicator"></span>
        |        <button class="nav-btn" id="next-btn" type="button">
        |          Next <i class="fa-solid fa-chevron-right"></i>
        |        </button>
        |      </div>
        |    </div>
        |  </div>
        |
        |  <footer>
        |    Generated from a Kotlin DSL via GitHub Actions &amp; deployed to GitHub Pages.
        |  </footer>
        |
        |  <script src="https://cdnjs.cloudflare.com/ajax/libs/pdf.js/3.11.174/pdf.min.js"></script>
        |  <script src="app.js"></script>
        |  <script src="pdf-viewer.js"></script>
        |  <script>init();</script>
        |</body>
        |</html>
    """.trimMargin()
}
