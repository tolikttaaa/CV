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
│       │       ├── latex/          # Model → compilable LaTeX directory
│       │       │   ├── LatexRenderer.kt
│       │       │   ├── LatexTemplate.kt # Extracts the bundled cls + fonts
│       │       │   └── LatexText.kt#   Inline markup → LaTeX, escaping
│       │       └── web/            # Model → build/cv-data.json
│       │           ├── WebRenderer.kt # Schema consumed by web/portfolio.js
│       │           ├── HtmlText.kt #   Inline markup → HTML fragments
│       │           └── JsonWriter.kt # Minimal dependency-free JSON writer
│       └── resources/…/template/   # Bundled cvdsl.cls + fonts
├── my-cv/                          # Module 2: the actual CV (depends on :cv-dsl)
│   └── src/main/
│       ├── resources/photo.jpg     # Profile photo
│       └── kotlin/cv/
│           ├── Main.kt             # Entry point: renders LaTeX + web data into build/
│           └── content/            # ★ The CV content, written in the DSL — edit these
│               ├── CvDefinition.kt #   Header, contacts, section order
│               ├── Summary.kt      #   One file per section, like latex/sections/ before
│               ├── Experience.kt
│               ├── Skills.kt
│               ├── PersonalProjects.kt
│               ├── Teaching.kt
│               ├── Education.kt
│               └── References.kt
├── web/
│   ├── index.html                  # GitHub Pages landing page
│   └── favicon.png                 # Site icon
├── build/                          # All build output (gitignored)
│   ├── latex/                      # Generated LaTeX sources + assets
│   ├── cv.pdf                      # Compiled CV
│   ├── cv-data.json                # Generated portfolio data
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

Requires JDK 21+ and LuaLaTeX (TeX Live 2022+).

```sh
./gradlew generatePdf     # Kotlin DSL → build/latex → build/cv.pdf (LuaLaTeX, 2 passes)
./gradlew generateWeb     # Kotlin DSL → build/cv-data.json
./gradlew assembleSite    # complete web page bundle → build/site
./gradlew serveSite       # all of the above + dev server at http://localhost:8080
./gradlew stopSite        # stop the dev server
```

If LuaLaTeX is not at `/Library/TeX/texbin/lualatex` or on `PATH`, pass `-PlualatexPath=/path/to/lualatex`.

## Deployment

Every push to `main` triggers the GitHub Actions workflow:

1. Runs the Kotlin generator: LaTeX sources → `build/latex`, portfolio data → `build/cv-data.json`
2. Compiles `build/latex/cv.tex` with LuaLaTeX inside a full TeX Live container
3. Assembles the site into `build/site` and pushes it to the `gh-pages` branch

GitHub Pages serves the result from the `gh-pages` branch (`/ root`).
