package cv.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/** Stops the preview process recorded by [ServeCvSiteTask]. */
abstract class StopCvSiteTask : DefaultTask() {
    @get:Internal
    abstract val pidFile: RegularFileProperty

    @TaskAction
    fun stop() {
        val stopped = stopRecordedProcess(pidFile.get().asFile)
        logger.lifecycle(if (stopped) "Stopped the CV preview server." else "No CV preview server is running.")
    }
}

internal fun stopRecordedProcess(pidFile: File): Boolean {
    if (!pidFile.isFile) return false
    val pid = pidFile.readText().trim().toLongOrNull()
    val process = pid?.let { ProcessHandle.of(it).orElse(null) }
    val stopped = process?.isAlive == true
    if (stopped) {
        process?.destroy()
        try {
            process?.onExit()?.get(STOP_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        } catch (_: TimeoutException) {
            process?.destroyForcibly()
        }
    }
    pidFile.delete()
    return stopped
}

private const val STOP_TIMEOUT_SECONDS = 2L
