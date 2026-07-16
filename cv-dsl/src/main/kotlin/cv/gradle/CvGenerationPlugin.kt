package cv.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.TaskProvider

/** Registers the reusable CV artifact pipeline on a Kotlin JVM consumer. */
class CvGenerationPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("cvGeneration", CvGenerationExtension::class.java).apply {
            mainClass.convention("cv.MainKt")
            lualatexExecutable.convention(defaultLualatex(project))
            previewPort.convention(
                project.providers.gradleProperty("cvPreviewPort")
                    .map(String::toInt)
                    .orElse(DEFAULT_PREVIEW_PORT),
            )
        }
        project.pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
            registerTasks(project, extension)
        }
    }

    private fun registerTasks(project: Project, extension: CvGenerationExtension) {
        val sourceSets = project.extensions.getByType(org.gradle.api.tasks.SourceSetContainer::class.java)
        val runtimeClasspath = sourceSets.getByName("main").runtimeClasspath
        val outputs = CvOutputs(project)
        val verify = registerEnvironmentVerification(project, extension)
        val latex = registerGenerator(project, extension, runtimeClasspath, "latex", outputs.latex)
        val web = registerGenerator(project, extension, runtimeClasspath, "web", outputs.web)
        val pdf = registerPdf(project, extension, outputs, verify, latex)
        val site = registerSiteAssembly(project, outputs, web, pdf)
        registerPreviewTasks(project, extension, outputs, verify, site)
    }

    private fun registerEnvironmentVerification(
        project: Project,
        extension: CvGenerationExtension,
    ): TaskProvider<VerifyCvEnvironmentTask> = project.tasks.register(
        "verifyCvEnvironment",
        VerifyCvEnvironmentTask::class.java,
    ) { task ->
        task.group = TASK_GROUP
        task.description = "Verifies that LuaLaTeX and the JDK preview server are available."
        task.lualatexExecutable.set(extension.lualatexExecutable)
    }

    private fun registerGenerator(
        project: Project,
        extension: CvGenerationExtension,
        runtimeClasspath: FileCollection,
        target: String,
        outputDirectory: Provider<Directory>,
    ): TaskProvider<JavaExec> = project.tasks.register(
        "generate${target.replaceFirstChar(Char::uppercase)}",
        JavaExec::class.java,
    ) { task ->
        task.group = TASK_GROUP
        task.description = "Generates $target CV files in build/$target."
        task.dependsOn(project.tasks.named("classes"))
        task.classpath = runtimeClasspath
        task.mainClass.set(extension.mainClass)
        task.workingDir = project.rootDir
        task.args(project.rootDir.absolutePath, target)
        task.outputs.dir(outputDirectory)
    }

    private fun registerPdf(
        project: Project,
        extension: CvGenerationExtension,
        outputs: CvOutputs,
        verify: TaskProvider<VerifyCvEnvironmentTask>,
        latex: TaskProvider<JavaExec>,
    ): TaskProvider<CompileCvPdfTask> = project.tasks.register(
        "generatePdf",
        CompileCvPdfTask::class.java,
    ) { task ->
        task.group = TASK_GROUP
        task.description = "Generates and compiles build/cv.pdf with two LuaLaTeX passes."
        task.dependsOn(verify, latex)
        task.lualatexExecutable.set(extension.lualatexExecutable)
        task.latexDirectory.set(outputs.latex)
        task.pdfFile.set(outputs.pdf)
        task.logFile.set(outputs.latexLog)
    }

    private fun registerSiteAssembly(
        project: Project,
        outputs: CvOutputs,
        web: TaskProvider<JavaExec>,
        pdf: TaskProvider<CompileCvPdfTask>,
    ): TaskProvider<Sync> = project.tasks.register("assembleSite", Sync::class.java) { task ->
        task.group = TASK_GROUP
        task.description = "Assembles the generated portfolio and PDF in build/site."
        task.dependsOn(web, pdf)
        task.from(outputs.web)
        task.from(outputs.pdf)
        task.into(outputs.site)
    }

    private fun registerPreviewTasks(
        project: Project,
        extension: CvGenerationExtension,
        outputs: CvOutputs,
        verify: TaskProvider<VerifyCvEnvironmentTask>,
        site: TaskProvider<Sync>,
    ) {
        project.tasks.register("serveSite", ServeCvSiteTask::class.java) { task ->
            task.group = TASK_GROUP
            task.description = "Assembles and serves the site on the configured local port."
            task.dependsOn(verify, site)
            task.siteDirectory.set(outputs.site)
            task.port.set(extension.previewPort)
            task.logFile.set(outputs.serverLog)
            task.pidFile.set(outputs.serverPid)
            task.outputs.upToDateWhen { false }
        }
        project.tasks.register("stopSite", StopCvSiteTask::class.java) { task ->
            task.group = TASK_GROUP
            task.description = "Stops the local site preview started by serveSite."
            task.pidFile.set(outputs.serverPid)
            task.outputs.upToDateWhen { false }
        }
    }

    private fun defaultLualatex(project: Project): Provider<String> =
        project.providers.gradleProperty("lualatexPath").orElse(
            project.providers.provider {
                MAC_TEX_LUALATEX.takeIf { project.file(it).isFile } ?: "lualatex"
            },
        )

    private companion object {
        const val TASK_GROUP = "cv"
        const val DEFAULT_PREVIEW_PORT = 8080
        const val MAC_TEX_LUALATEX = "/Library/TeX/texbin/lualatex"
    }
}

/** Lazily resolved output locations shared by the registered tasks. */
private class CvOutputs(project: Project) {
    private val buildRoot = project.rootProject.layout.buildDirectory
    val latex: Provider<Directory> = buildRoot.dir("latex")
    val web: Provider<Directory> = buildRoot.dir("web")
    val site: Provider<Directory> = buildRoot.dir("site")
    val pdf: Provider<RegularFile> = buildRoot.file("cv.pdf")
    val latexLog: Provider<RegularFile> = buildRoot.file("lualatex.log")
    val serverLog: Provider<RegularFile> = buildRoot.file("site-server.log")
    val serverPid: Provider<RegularFile> = buildRoot.file("site-server.pid")
}
