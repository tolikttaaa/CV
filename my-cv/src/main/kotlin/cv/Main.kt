package cv

import cv.content.anatoliiCv
import cv.generation.CvApplication

/**
 * Generates CV artifacts from the Kotlin DSL definition.
 *
 * Usage: `Main [root] [target]`
 *  - `root` — repository root the `build/` directory lives under (default: `.`)
 *  - `target` — what to generate:
 *      - `latex` — `build/latex/`: complete LaTeX source directory (generated
 *        sections, bundled document class and fonts, profile photo)
 *      - `web` — `build/web/`: generated static portfolio files
 *      - `all` (default) — both
 *
 * Normally invoked through the Gradle tasks `generateLatex` / `generateWeb` / `run`.
 */
fun main(args: Array<String>) = CvApplication(anatoliiCv).run(args)
