package cv.generation

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

/** Supplies content-owned files that must accompany a generated CV. */
fun interface CvAssetSource {
    /** Copies the asset at [path] to [target], replacing an existing file. */
    fun copy(path: String, target: Path)
}

/** Loads CV assets from the consumer application's runtime classpath. */
class ClasspathCvAssetSource(
    private val classLoader: ClassLoader = Thread.currentThread().contextClassLoader,
) : CvAssetSource {
    override fun copy(path: String, target: Path) {
        val resourcePath = path.removePrefix("/")
        val stream = classLoader.getResourceAsStream(resourcePath)
            ?: error("Bundled CV asset /$resourcePath not found")

        stream.use {
            target.parent?.let(Files::createDirectories)
            Files.copy(it, target, StandardCopyOption.REPLACE_EXISTING)
        }
    }
}
