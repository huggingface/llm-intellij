package co.huggingface.llmintellij.lsp

import co.huggingface.llmintellij.LlmSettingsState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor
import io.ktor.util.*
import org.eclipse.lsp4j.services.LanguageServer
import java.io.*
import java.net.*
import java.nio.file.Files
import java.util.zip.GZIPInputStream
import kotlin.io.path.Path
import kotlin.io.path.deleteIfExists


class LlmLsLspServerDescriptor(project: Project) : ProjectWideLspServerDescriptor(project, "LlmLs") {
    private val logger = Logger.getInstance("llmLsLspServerDescriptor")

    override fun isSupportedFile(file: VirtualFile) = true

    override fun createCommandLine(): GeneralCommandLine {
        val settings = LlmSettingsState.instance
        val binaryPath = downloadLlmLs(logger, settings.lsp.binaryPath, settings.lsp.version) ?: throw Error("llm-ls binary path is not set")
        settings.lsp.binaryPath = binaryPath
        return GeneralCommandLine().apply {
            exePath = binaryPath
            withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE)
            withCharset(Charsets.UTF_8)
            withEnvironment("LLM_LOG_LEVEL", settings.lsp.logLevel)
            addParameter("--stdio")
        }
    }

    override val lsp4jServerClass: Class<out LanguageServer> = LlmLsLanguageServer::class.java

    override val lspGoToDefinitionSupport = false

    override val lspCompletionSupport = null

    override val lspDiagnosticsSupport = null

    override val lspCodeActionsSupport = null

    override val lspCommandsSupport = null
}

fun isWindows(os: String): Boolean {
    return os.toLowerCasePreservingASCIIRules().indexOf("win") >= 0
}

fun isMac(os: String): Boolean {
    return os.toLowerCasePreservingASCIIRules().indexOf("mac") >= 0
}

fun isUnix(os: String): Boolean {
    val osLower = os.toLowerCasePreservingASCIIRules()
    return osLower.indexOf("nix") >= 0 || osLower.indexOf("nux") >= 0 || osLower.indexOf("aix") > 0
}

fun buildBinaryName(logger: Logger): String? {
    val os = System.getProperty("os.name")

    var arch = System.getProperty("os.arch")
    if (arch == "amd64") {
        arch = "x86_64"
    }

    var osSuffix: String? = null
    if (isMac(os)) {
        osSuffix = "apple-darwin"
    } else if (isUnix(os)) {
        osSuffix = "unknown-linux-gnu"
    } else if (isWindows(os)) {
        osSuffix = "pc-windows-msvc"
    }

    if (osSuffix == null) {
        logger.error("Unsupported architecture or OS: $arch $os")
        return null
    }

    return "llm-ls-$arch-$osSuffix"
}

fun buildUrl(binName: String, version: String): String {
    return "https://github.com/huggingface/llm-ls/releases/download/$version/$binName.gz"
}

fun downloadFile(logger: Logger, urlString: String, outputPath: String) {
    try {
        val url = URL(urlString)

        // Open a connection
        val connection = url.openConnection() as HttpURLConnection

        // Set up connection properties
        connection.requestMethod = "GET"
        connection.connectTimeout = 5000 // Adjust timeout as needed

        // Connect to the URL
        connection.connect()

        // Check if the connection is successful (HTTP 200 OK)
        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            // Open input stream from the connection
            val inputStream = connection.inputStream

            // Create output stream to write the file
            val outputStream = FileOutputStream(outputPath)

            // Buffer for reading from input stream
            val buffer = ByteArray(1024)
            var bytesRead: Int

            // Read from input stream and write to output stream
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            // Close streams
            inputStream.close()
            outputStream.close()

            logger.info("File downloaded successfully.")
        } else {
            logger.error("Failed to download file. Response Code: ${connection.responseCode}")
        }

        // Disconnect the connection
        connection.disconnect()
    } catch (e: Exception) {
        logger.error("Error: ${e.message}")
    }
}


fun downloadAndUnzip(logger: Logger, url: String, binDir: File, binName: String, targetPath: String) {
    val extractedBinPath = File(binDir, binName).absolutePath
    val zipPath = "$extractedBinPath.gz"

    downloadFile(logger, url, zipPath)

    try {
        val inputByteStream = FileInputStream(zipPath)
        val outputByteStream = FileOutputStream(extractedBinPath)

        outputByteStream.write(GZIPInputStream(inputByteStream).use { it.readBytes() })
        inputByteStream.close()
        outputByteStream.close()
        logger.info("Successfully extracted llm-ls")
    } catch (e: Exception)
    {
        logger.error("Gzip exception: $e")
    }

    try {
        Files.move(Path(extractedBinPath), Path(targetPath))
    } catch (e: Exception) {
        logger.error("Move failed: $e")
    }

    try {
        Path(targetPath).toFile().setExecutable(true, false)
    } catch (e: Exception) {
        logger.error("Set file permissions failed: $e")
    }

    Path(zipPath).deleteIfExists()
}

fun downloadLlmLs(logger: Logger, binaryPath: String?, version: String): String? {
    if (binaryPath != null && binaryPath.endsWith(version) && File(binaryPath).exists()) {
        return binaryPath
    }

    val binDir = File(System.getProperty("user.home"), ".cache/llm_intellij/bin")
    binDir.mkdirs()

    val binName = buildBinaryName(logger) ?: return null

    val fullPath = File(binDir, "$binName-$version")

    if (!fullPath.exists()) {
        val url = buildUrl(binName, version)
        downloadAndUnzip(logger, url, binDir, binName, fullPath.absolutePath)
        logger.info("Successfully downloaded llm-ls")
    }

    return fullPath.absolutePath
}
