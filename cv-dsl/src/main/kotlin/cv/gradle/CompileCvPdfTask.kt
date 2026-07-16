package cv.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/** Compiles generated LaTeX sources twice so references and page data settle. */
abstract class CompileCvPdfTask : DefaultTask() {
    @get:Input
    abstract val lualatexExecutable: Property<String>

    @get:InputDirectory
    abstract val latexDirectory: DirectoryProperty

    @get:OutputFile
    abstract val pdfFile: RegularFileProperty

    @get:OutputFile
    abstract val logFile: RegularFileProperty

    @TaskAction
    fun compile() {
        val latexDir = latexDirectory.get().asFile
        val mainFile = latexDir.resolve("cv.tex")
        if (!mainFile.isFile) {
            throw GradleException("Generated LaTeX entry point is missing: $mainFile")
        }

        val outputDir = pdfFile.get().asFile.parentFile.apply { mkdirs() }
        val log = logFile.get().asFile
        repeat(PASSES) { pass ->
            val exitCode = ProcessBuilder(
                lualatexExecutable.get(),
                "-interaction=nonstopmode",
                "-output-directory=${outputDir.absolutePath}",
                mainFile.name,
            )
                .directory(latexDir)
                .redirectOutput(log)
                .redirectErrorStream(true)
                .start()
                .waitFor()
            if (exitCode != 0) {
                throw GradleException(
                    "LuaLaTeX pass ${pass + 1} failed with exit code $exitCode. See $log",
                )
            }
        }
        logger.lifecycle("Compiled ${pdfFile.get().asFile}")
    }

    private companion object {
        const val PASSES = 2
    }
}
