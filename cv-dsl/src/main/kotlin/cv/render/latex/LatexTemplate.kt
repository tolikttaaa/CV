package cv.render.latex

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

/**
 * The LaTeX template bundled with the library: the `cvdsl.cls`
 * document class (whose commands — `\work`, `\keywordsentry`, `\educationentry`,
 * … — [LatexRenderer] targets) and the Source Sans Pro font files it loads
 * with the `localFont` option.
 *
 * Shipping the template as classpath resources keeps the library
 * self-contained: consumers only provide content (and a photo) and get a
 * compilable LaTeX directory out.
 */
internal object LatexTemplate {

    private const val RESOURCE_ROOT = "/cv/render/latex/template"

    /**
     * Bundled files, relative to [RESOURCE_ROOT] and to the extraction
     * directory. Kept as an explicit list because classpath directories
     * cannot be enumerated portably (directory vs. jar packaging).
     */
    private val FILES = listOf("cvdsl.cls") + listOf(
        "Black", "BlackIt", "Bold", "BoldIt", "ExtraLight", "ExtraLightIt",
        "It", "Light", "LightIt", "Regular", "Semibold", "SemiboldIt",
    ).map { "fonts/SourceSansPro-$it.otf" }

    /** Extracts the document class and fonts into [outDir], overwriting stale copies. */
    fun extractTo(outDir: Path) {
        for (file in FILES) {
            val resource = "$RESOURCE_ROOT/$file"
            val stream = javaClass.getResourceAsStream(resource)
                ?: error("Bundled LaTeX template resource not found: $resource")
            stream.use {
                val target = outDir.resolve(file)
                Files.createDirectories(target.parent)
                Files.copy(it, target, StandardCopyOption.REPLACE_EXISTING)
            }
        }
    }
}
