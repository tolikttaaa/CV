package cv.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.net.InetSocketAddress
import java.net.Socket

/** Starts a detached JDK web server for the assembled site. */
abstract class ServeCvSiteTask : DefaultTask() {
    @get:InputDirectory
    abstract val siteDirectory: DirectoryProperty

    @get:Input
    abstract val port: Property<Int>

    @get:Internal
    abstract val logFile: RegularFileProperty

    @get:Internal
    abstract val pidFile: RegularFileProperty

    @TaskAction
    fun serve() {
        stopRecordedProcess(pidFile.get().asFile)
        if (canConnect(port.get())) {
            throw GradleException(
                "Port ${port.get()} is already in use. Stop the existing service or choose another port " +
                    "with -PcvPreviewPort=<port>.",
            )
        }
        val log = logFile.get().asFile.apply { parentFile.mkdirs() }
        val process = ProcessBuilder(
            jwebserverExecutable().absolutePath,
            "-b", HOST,
            "-p", port.get().toString(),
            "-d", siteDirectory.get().asFile.absolutePath,
        )
            .redirectOutput(log)
            .redirectErrorStream(true)
            .start()
        pidFile.get().asFile.writeText(process.pid().toString())

        if (!waitForServer(port.get())) {
            process.destroyForcibly()
            val details = log.takeIf { it.isFile }?.readText()?.trim().orEmpty()
            throw GradleException("Preview server failed to start. See $log\n$details")
        }
        logger.lifecycle("Serving build/site at http://$HOST:${port.get()}/ (stop with: ./gradlew stopSite)")
    }

    private fun waitForServer(port: Int): Boolean {
        val deadline = System.currentTimeMillis() + STARTUP_TIMEOUT_MILLIS
        while (System.currentTimeMillis() < deadline) {
            if (canConnect(port)) return true
            Thread.sleep(RETRY_DELAY_MILLIS)
        }
        return false
    }

    private fun canConnect(port: Int): Boolean = runCatching {
        Socket().use { it.connect(InetSocketAddress(HOST, port), CONNECT_TIMEOUT_MILLIS) }
    }.isSuccess

    private companion object {
        const val HOST = "127.0.0.1"
        const val STARTUP_TIMEOUT_MILLIS = 10_000L
        const val CONNECT_TIMEOUT_MILLIS = 500
        const val RETRY_DELAY_MILLIS = 200L
    }
}
