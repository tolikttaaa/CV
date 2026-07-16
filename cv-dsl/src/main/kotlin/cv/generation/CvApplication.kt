package cv.generation

import cv.model.Cv
import cv.render.CvRendererFactory
import cv.render.RenderFormat
import java.nio.file.Path

/** Output selections understood by the reusable CV command-line application. */
enum class GenerationTarget {
    Latex,
    Web,
    All,
    ;

    internal val formats: List<RenderFormat>
        get() = when (this) {
            Latex -> listOf(RenderFormat.Latex)
            Web -> listOf(RenderFormat.Web)
            All -> listOf(RenderFormat.Latex, RenderFormat.Web)
        }

    companion object {
        /** Parses the stable lower-case value accepted by Gradle generation tasks. */
        fun parse(value: String): GenerationTarget = entries.firstOrNull {
            it.name.equals(value, ignoreCase = true)
        } ?: throw IllegalArgumentException(
            "Unknown target \"$value\" — expected latex, web or all",
        )
    }
}

/**
 * Reusable entry point that renders a [Cv] and copies its content-owned assets.
 *
 * Consumer modules only need to construct this class with their DSL model and
 * delegate their `main` function to [run]. By default, assets such as the
 * profile photo are loaded from the consumer's runtime classpath.
 */
class CvApplication(
    private val cv: Cv,
    private val assetSource: CvAssetSource = ClasspathCvAssetSource(),
    private val report: (String) -> Unit = ::println,
) {
    /**
     * Generates the target selected by `[root, target]` command-line arguments.
     * Both arguments are optional and default to `.` and `all`, respectively.
     */
    fun run(args: Array<String>) {
        val root = Path.of(args.getOrElse(0) { "." }).toAbsolutePath().normalize()
        val target = GenerationTarget.parse(args.getOrElse(1) { "all" })
        generate(root, target)
    }

    /** Generates [target] beneath the `build` directory of [root]. */
    fun generate(root: Path, target: GenerationTarget = GenerationTarget.All) {
        target.formats.forEach { format ->
            generateFormat(root.toAbsolutePath().normalize(), format)
        }
    }

    private fun generateFormat(root: Path, format: RenderFormat) {
        val formatName = format.directoryName
        val outputDirectory = root.resolve("build").resolve(formatName)
        CvRendererFactory.create(format).render(cv, outputDirectory)
        cv.photo?.let { photo ->
            val photoTarget = outputDirectory.resolve(photo.file).normalize()
            require(photoTarget.startsWith(outputDirectory)) {
                "CV asset path must remain inside the $formatName output directory: ${photo.file}"
            }
            assetSource.copy(photo.file, photoTarget)
        }
        report("Generated ${format.displayName} sources in $outputDirectory")
    }
}

private val RenderFormat.directoryName: String
    get() = when (this) {
        RenderFormat.Latex -> "latex"
        RenderFormat.Web -> "web"
    }

private val RenderFormat.displayName: String
    get() = when (this) {
        RenderFormat.Latex -> "LaTeX"
        RenderFormat.Web -> "web portfolio"
    }
