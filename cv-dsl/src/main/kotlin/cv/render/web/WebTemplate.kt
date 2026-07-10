package cv.render.web

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

/** Browser assets bundled with the reusable web renderer. */
internal object WebTemplate {

    private const val RESOURCE_ROOT = "/cv/render/web/template"
    private val files = listOf("styles.css", "app.js", "pdf-viewer.js", "favicon.png")

    /** Extracts all assets into [outDir], overwriting stale copies. */
    fun extractTo(outDir: Path) {
        for (file in files) {
            val resource = "$RESOURCE_ROOT/$file"
            val stream = javaClass.getResourceAsStream(resource)
                ?: error("Bundled web template resource not found: $resource")
            stream.use {
                val target = outDir.resolve(file)
                Files.createDirectories(target.parent)
                Files.copy(it, target, StandardCopyOption.REPLACE_EXISTING)
            }
        }
    }
}
