package cv.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.IOException

/** Verifies external tools used by PDF compilation and site preview. */
abstract class VerifyCvEnvironmentTask : DefaultTask() {
    @get:Input
    abstract val lualatexExecutable: Property<String>

    @TaskAction
    fun verify() {
        verifyLualatex()
        verifyJwebserver()
    }

    private fun verifyLualatex() {
        val executable = lualatexExecutable.get()
        val process = try {
            ProcessBuilder(executable, "--version")
                .redirectErrorStream(true)
                .start()
        } catch (_: IOException) {
            throw GradleException(lualatexMissingMessage(executable))
        }
        val firstLine = process.inputStream.bufferedReader().use { it.readLine() }
        val exitCode = process.waitFor()
        if (exitCode != 0) {
            throw GradleException(lualatexMissingMessage(executable))
        }
        logger.lifecycle("LuaLaTeX available: ${firstLine ?: executable}")
    }

    private fun verifyJwebserver() {
        val executable = jwebserverExecutable()
        if (!executable.isFile || !executable.canExecute()) {
            throw GradleException(
                "JDK jwebserver was not found at $executable. " +
                    "Run Gradle with a full JDK 18 or newer (JDK 21 is recommended).",
            )
        }
        logger.lifecycle("jwebserver available: $executable")
    }

    private fun lualatexMissingMessage(executable: String): String =
        "LuaLaTeX is unavailable ('$executable'). Install TeX Live/MacTeX, ensure lualatex is on PATH, " +
            "or pass -PlualatexPath=/absolute/path/to/lualatex."
}

internal fun jwebserverExecutable(): File =
    File(System.getProperty("java.home"), "bin/jwebserver")
