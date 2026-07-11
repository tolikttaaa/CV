# Anatolii Anishchenko — CV

A Kotlin DSL is the single source of truth for this CV. The project renders the
same immutable model into:

- a LuaLaTeX document and PDF;
- a static portfolio site with no intermediate JSON or runtime data fetch.

GitHub Actions verifies the Kotlin code, compiles the PDF, assembles the site,
and deploys it to GitHub Pages.

## Project layout

```text
├── gradle/libs.versions.toml       # Java, Kotlin and Detekt versions
├── cv-dsl/                         # Reusable, content-independent library
│   └── src/main/
│       ├── kotlin/cv/
│       │   ├── model/              # Immutable CV model
│       │   ├── dsl/                # Type-safe cv { ... } builders
│       │   └── render/
│       │       ├── latex/          # LaTeX renderer implementation
│       │       └── web/            # Static HTML renderer implementation
│       └── resources/cv/render/
│           ├── latex/template/     # Document class and local fonts
│           └── web/template/       # CSS, JavaScript and favicon
├── my-cv/                          # This CV's content and generation entry point
│   └── src/main/
│       ├── kotlin/cv/
│       │   ├── content/            # Header and section definitions
│       │   └── dictionaries/       # Organizations and institutions used by this CV
│       └── resources/photo.jpg     # Profile photo
├── build/                          # Generated artifacts; never edited manually
└── .github/workflows/              # Verification and Pages deployment
```

`cv-dsl` contains no personal CV content and can be extracted as a standalone
library. `my-cv` depends on it and supplies the actual document definition.

## Rendering architecture

The render layer keeps the model independent from HTML and LaTeX:

1. `CvRenderer` defines the public `render(cv, outputDirectory)` operation.
2. `RenderFormat` is the closed set of supported formats: `Web` and `Latex`.
3. `CvRendererFactory` selects the complete renderer for a format.
4. `RendererBundle<C>` is the composition root for a format. It requires a
   renderer for every supported section and leaf element.
5. `SectionRenderer<C>` performs exhaustive dispatch over the sealed section
   hierarchy.
6. `ElementRenderer<E, C>` is implemented by format-specific renderers such as
   `WebProjectRenderer` and `LatexProjectRenderer`.

```kotlin
val renderer = CvRendererFactory.create(RenderFormat.Web)
renderer.render(cv, outputDirectory)
```

Both `WebRendererBundle` and `LatexRendererBundle` must satisfy the same bundle
contract. If a required renderer is missing, compilation fails. Web renderers
receive `WebRenderContext`; LaTeX renderers use `Unit` because section rendering
does not require shared state.

### Adding a model element

When introducing a new renderable element:

1. Add the immutable model and its DSL builder operation.
2. Add its `ElementRenderer` property to `RendererBundle`.
3. Implement and register it in both renderer bundles.
4. Add renderer-focused tests or compare generated output where appropriate.

For a new section type, also extend `SectionRenderer` and its exhaustive
`Section.renderWith` dispatch. Kotlin then forces both formats to support it.

## Editing the CV

Personal content lives in `my-cv/src/main/kotlin/cv/content/`:

- `CvDefinition.kt` defines identity, photo, contacts, section order and footer;
- the remaining files define one section each.

Rich text is written as plain content and styled through matching rules:

```kotlin
paragraph(
    """
    Software Engineer experienced with Kotlin and ITMO University.
    """,
) {
    bold("Kotlin")
    highlight("ITMO University", linkTo("https://en.itmo.ru/"), bold)
}
```

Supported styles include `bold`, `italic`, `nowrap`, `colored(...)`, and
`linkTo(...)`. A highlight rule that matches nothing fails generation, which
prevents formatting from silently disappearing after text changes. Renderers
escape HTML and LaTeX special characters automatically.

Contacts are declared in visual rows. The web representation flattens those
rows into its contact card, while LaTeX preserves the row boundaries:

```kotlin
social {
    row {
        linkedin("ttaaa")
        github("tolikttaaa")
        leetcode("ttaaa")
    }
}
```

## Building locally

Requirements:

- JDK matching the `java` version in `gradle/libs.versions.toml`;
- LuaLaTeX from TeX Live 2022 or newer for PDF generation.

Important tasks:

| Task | Purpose | Output |
|---|---|---|
| `check` | Compile, test and run Detekt in both modules | Verification result |
| `detekt` | Run static analysis only | `<module>/build/reports/detekt/` |
| `generateLatex` | Render the DSL to LaTeX sources | `build/latex/` |
| `generatePdf` | Render and compile the PDF in two passes | `build/cv.pdf` |
| `generateWeb` | Render complete HTML and extract browser assets | `build/web/` |
| `assembleSite` | Combine the portfolio, photo and PDF | `build/site/` |
| `serveSite` | Assemble and serve the site on port 8080 | Local HTTP server |
| `stopSite` | Stop the local server | — |

```sh
./gradlew check
./gradlew generatePdf
./gradlew serveSite
./gradlew stopSite
```

Run `./gradlew tasks --group cv` for the CV-specific task list. The application
entry point also supports direct generation:

```text
Main [repository-root] [latex|web|all]
```

If LuaLaTeX is not installed at the standard MacTeX path and is not available
on `PATH`, override it with `-PlualatexPath=/absolute/path/to/lualatex`.

The preview server uses the JDK's `jwebserver`, runs detached, and writes startup
failures to `build/site-server.log`.

## Version management

Java, Kotlin and Detekt versions are centralized in
`gradle/libs.versions.toml`. Module build scripts consume catalog aliases and
must not declare their own plugin or toolchain versions.

Detekt runs with its default rule set, reports in Checkstyle, HTML, SARIF and
Markdown formats, and is part of every `check` invocation. Narrow suppressions
are used only for intentionally declarative DSL structures.

## Deployment

Every push to `main`, and every manual workflow dispatch, performs:

1. `./gradlew check`;
2. generation of LaTeX and the static portfolio;
3. PDF compilation with LuaLaTeX;
4. assembly of `build/site`;
5. deployment to the `gh-pages` branch.

GitHub Pages serves the contents of that branch from its root.
