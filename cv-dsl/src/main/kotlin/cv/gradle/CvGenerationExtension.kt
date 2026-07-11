package cv.gradle

import org.gradle.api.provider.Property

/** Consumer-facing configuration for the reusable CV generation tasks. */
abstract class CvGenerationExtension {
    /** Main class that accepts `[root, latex|web|all]`; defaults to `cv.MainKt`. */
    abstract val mainClass: Property<String>

    /** LuaLaTeX executable name or absolute path. */
    abstract val lualatexExecutable: Property<String>

    /** Local port used by `serveSite`. */
    abstract val previewPort: Property<Int>
}
