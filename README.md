# Anatolii Anishchenko — CV

A Kotlin DSL is the single source of truth for this CV. The project renders the
same immutable model into:

- a LuaLaTeX document and PDF;
- a static portfolio site with no intermediate JSON or runtime data fetch.

GitHub Actions verifies the Kotlin code, compiles the PDF, assembles the site,
and deploys it to GitHub Pages.

## Project layout

```text
├── gradle/libs.versions.toml       # Build-tool and artifact versions
├── cv-dsl/                         # Standalone included build
│   ├── settings.gradle.kts         # Independent build identity and repositories
│   ├── gradle/libs.versions.toml   # Versions owned by the reusable build
│   └── src/main/
│       ├── kotlin/cv/
│       │   ├── gradle/             # Reusable cv.dsl.generation plugin
│       │   ├── generation/         # Reusable command-line generation pipeline
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

`cv-dsl` contains no personal CV content. The root build includes it through
`pluginManagement` for the generation plugin and as a normal composite build
for dependency substitution of `cv.dsl:cv-dsl`. `my-cv` therefore uses the same
contracts it would use with published artifacts.

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

`CvApplication` sits above the individual renderers. It parses the common CLI,
selects formats, owns the output layout, and copies content assets from an
injectable `CvAssetSource`. The consumer entry point therefore only supplies
its model:

```kotlin
fun main(args: Array<String>) = CvApplication(anatoliiCv).run(args)
```

By default, a photo declared as `photo("photo.jpg")` is loaded from the
consumer's runtime classpath. Consumers with another asset store can pass a
custom `CvAssetSource` without changing the generation pipeline.

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

## Gradle generation plugin

`cv-dsl` defines the `cv.dsl.generation` plugin alongside the model and
renderers. A consumer applies the plugin and depends on the library:

```kotlin
plugins {
    kotlin("jvm")
    application
    id("cv.dsl.generation")
}

dependencies {
    implementation("cv.dsl:cv-dsl:<version>")
}

application {
    mainClass.set("cv.MainKt")
}
```

The plugin expects that main class to accept `[repositoryRoot, target]`, where
the target is `latex` or `web`. It owns the complete pipeline:

- `verifyCvEnvironment` checks LuaLaTeX and the JDK `jwebserver` executable;
- `generateLatex` and `generateWeb` invoke the consumer's generator;
- `generatePdf` verifies tools, generates LaTeX, and runs LuaLaTeX twice;
- `assembleSite` combines generated web files and the PDF;
- `serveSite` and `stopSite` manage a PID-tracked local preview process.

Optional consumer configuration:

```kotlin
cvGeneration {
    mainClass.set("example.MainKt")
    lualatexExecutable.set("/opt/texlive/bin/lualatex")
    previewPort.set(9090)
}
```

Command-line overrides are also available:

```sh
./gradlew generatePdf -PlualatexPath=/absolute/path/to/lualatex
./gradlew serveSite -PcvPreviewPort=9090
```

Missing tools and occupied ports fail early with installation or override
instructions. Preview management uses only JDK APIs; it does not require
`bash`, `lsof`, or `xargs`.

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
- LuaLaTeX from TeX Live 2022 or newer for PDF generation. Web-only generation
  does not require a TeX installation.

Important tasks:

| Task | Purpose | Output |
|---|---|---|
| `check` | Compile, test and run Detekt in both modules | Verification result |
| `detekt` | Run static analysis only | `<module>/build/reports/detekt/` |
| `verifyCvEnvironment` | Check LuaLaTeX and `jwebserver` | Diagnostic output |
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

The preview server uses the JDK's `jwebserver`, records its PID in
`build/site-server.pid`, and writes output to `build/site-server.log`.

## Version management

Each Gradle build owns an independent version catalog:

- `gradle/libs.versions.toml` configures the root application build and the
  `cv-dsl` version consumed by `my-cv`;
- `cv-dsl/gradle/libs.versions.toml` configures the reusable library/plugin
  build and its published artifact version.

Build scripts consume catalog aliases and do not declare plugin or toolchain
versions directly. Composite dependency substitution ignores the requested
library version locally; when publishing, update the consumer version only when
`my-cv` is ready to adopt that release.

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
