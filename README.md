# Anatolii Anishchenko — CV

CV described with a Kotlin DSL — the single source of truth — rendered to a LaTeX/PDF document and to a portfolio web page, with automatic compilation and deployment to GitHub Pages via GitHub Actions.

## Structure

```
├── settings.gradle.kts             # Gradle multi-module build: :cv-dsl + :my-cv
├── cv-dsl/                         # Module 1: reusable, content-free CV toolkit
│   └── src/main/
│       ├── kotlin/cv/
│       │   ├── model/              # Immutable CV data model
│       │   │   ├── Cv.kt           #   Document root
│       │   │   ├── Section.kt      #   Section types and their entries
│       │   │   ├── RichText.kt     #   Inline markup (bold, links, colors, …)
│       │   │   └── Social.kt       #   Contact entries
│       │   ├── dsl/                # Type-safe builders (cv { … })
│       │   │   ├── CvBuilder.kt    #   Root builder + section methods
│       │   │   ├── SectionDsl.kt   #   Works/skills/projects/education/references builders
│       │   │   ├── SocialDsl.kt    #   Contact block builder
│       │   │   └── TextDsl.kt      #   Rich text and paragraph/bullet builders
│       │   └── render/
│       │       ├── CvRenderer.kt   # Common complete-renderer contract
│       │       ├── CvRendererFactory.kt # Sealed-format renderer selection
│       │       ├── RenderFormat.kt # Supported Web and LaTeX formats
│       │       ├── SectionRenderer.kt # Shared typed section visitor
│       │       ├── ElementRenderer.kt # Generic model-element contract
│       │       ├── RendererBundle.kt # Compile-time-complete renderer set
│       │       ├── latex/          # Model → compilable LaTeX directory
│       │       │   ├── LatexRenderer.kt # Small public generation entry point
│       │       │   ├── LatexDocumentRenderer.kt # Root document shell
│       │       │   ├── *LatexRenderer.kt # Type-specific section render functions
│       │       │   ├── LatexComponents.kt # Contacts, organizations and block content
│       │       │   ├── LatexTemplate.kt # Extracts the bundled cls + fonts
│       │       │   └── LatexText.kt # Inline markup → LaTeX, escaping
│       │       └── web/            # Model → complete static portfolio
│       │           ├── WebRenderer.kt # Small public generation entry point
│       │           ├── WebDocumentRenderer.kt # Shared page shell
│       │           ├── *WebRenderer.kt # Type-specific section render functions
│       │           ├── WebComponents.kt # Contacts, organizations, tags and titles
│       │           ├── WebTemplate.kt # Extracts bundled browser assets
│       │           └── HtmlText.kt #   Inline markup → HTML fragments
│       └── resources/cv/render/
│           ├── latex/template/     # Bundled cvdsl.cls + fonts
│           └── web/template/       # Bundled CSS, JavaScript and favicon
├── my-cv/                          # Module 2: the actual CV (depends on :cv-dsl)
│   └── src/main/
│       ├── resources/photo.jpg     # Profile photo
│       └── kotlin/cv/
│           ├── Main.kt             # Entry point: renders LaTeX + the static site into build/
│           └── content/            # ★ The CV content, written in the DSL — edit these
│               ├── CvDefinition.kt #   Header, contacts, section order
│               ├── Summary.kt      #   One file per section, like latex/sections/ before
│               ├── Experience.kt
│               ├── Skills.kt
│               ├── PersonalProjects.kt
│               ├── Teaching.kt
│               ├── Education.kt
│               └── References.kt
├── build/                          # All build output (gitignored)
│   ├── latex/                      # Generated LaTeX sources + assets
│   ├── cv.pdf                      # Compiled CV
│   ├── web/                        # Generated static portfolio (HTML + assets)
│   └── site/                       # Assembled site (deployed to Pages)
└── .github/workflows/
    └── build-deploy.yml            # Generate → compile → deploy pipeline
```

## Editing the CV

All content lives in `my-cv/src/main/kotlin/cv/content/` — the header and section order in `CvDefinition.kt`, each section body in its own file. Inline markup is available inside any text block:

```kotlin
paragraph(
    """
    Text is written as a natural multiline string — line breaks and
    indentation collapse to single spaces. It can mention Azul or
    link to ITMO University without any inline markup.
    """,
) {
    bold("Azul")                                              // literal match, single style
    highlight("ITMO University", linkTo("https://en.itmo.ru/"), bold)  // combined styles
    italic(Regex("without .* markup"))                        // regex match
}
```

Available styles: `bold`, `italic`, `nowrap` (keep on one line), `colored(CvColor.X)`, `linkTo(url)`. A highlight rule that matches nothing fails the build, so formatting cannot silently break when text is reworded. LaTeX special characters (`& % _ # …`) are escaped automatically by the renderers — write plain text.

## Building locally

Requires JDK 21+ and LuaLaTeX (TeX Live 2022+). The pipeline is a set of Gradle
tasks in the `cv` group (`./gradlew tasks --group cv`):

| Task           | Depends on                 | Produces                                        |
|----------------|----------------------------|-------------------------------------------------|
| `generateLatex`| —                          | `build/latex/` — LaTeX sources + template + photo |
| `generatePdf`  | `generateLatex`            | `build/cv.pdf` (two LuaLaTeX passes; log in `build/lualatex.log`) |
| `generateWeb`  | —                          | `build/web/` — generated HTML and browser assets |
| `assembleSite` | `generateWeb`, `generatePdf` | `build/site/` — deployable web page bundle    |
| `serveSite`    | `assembleSite`             | dev server at http://localhost:8080             |
| `stopSite`     | —                          | stops the dev server                            |

```sh
./gradlew generatePdf     # just the PDF
./gradlew serveSite       # everything + local preview
./gradlew stopSite        # stop the preview server
```

Notes:
- If LuaLaTeX is not at `/Library/TeX/texbin/lualatex` or on `PATH`, pass `-PlualatexPath=/path/to/lualatex`.
- The dev server is the JDK's own `jwebserver`, started detached — it survives
  Gradle daemon restarts and is verified to answer before the task succeeds
  (failures print `build/site-server.log`). `./gradlew run` still generates
  both LaTeX and the complete web portfolio without compiling the PDF.

## Deployment

Every push to `main` triggers the GitHub Actions workflow:

1. Runs the Kotlin generator: LaTeX sources → `build/latex`, static portfolio → `build/web`
2. Compiles `build/latex/cv.tex` with LuaLaTeX inside a full TeX Live container
3. Assembles the site into `build/site` and pushes it to the `gh-pages` branch

GitHub Pages serves the result from the `gh-pages` branch (`/ root`).
