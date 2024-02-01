package co.huggingface.llmintellij.lsp

import co.huggingface.llmintellij.LlmSettingsState
import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.OSProcessHandler
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor
import com.intellij.util.concurrency.annotations.RequiresBackgroundThread
import io.ktor.util.*
import org.eclipse.lsp4j.services.LanguageServer
import java.io.File

class LlmLsLspServerDescriptor(project: Project) : ProjectWideLspServerDescriptor(project, "LlmLs") {
    private val logger = Logger.getInstance("llmLsLspServerDescriptor")

    @RequiresBackgroundThread
    @Throws(ExecutionException::class)
    override fun startServerProcess(): OSProcessHandler {
        val startingCommandLine = createCommandLine()
        LOG.info("$this: starting LSP server: $startingCommandLine")
        return LLMOsProcessHandler(startingCommandLine)
    }

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
    val arch = System.getProperty("os.arch")

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

fun downloadAndUnzip(url: String, binDir: File, binName: String, fullPath: String) {
    val path = File(binDir, binName).absolutePath
    val downloadCommand = "curl -L -o $path.gz $url"
    val unzipCommand = "gunzip $path.gz"
    val renameCommand = "mv $path $fullPath"
    val chmodCommand = "chmod +x $fullPath"
    val cleanZipCommand = "rm $path.gz"

    runCommand(downloadCommand)
    runCommand(unzipCommand)
    runCommand(renameCommand)
    runCommand(chmodCommand)
    runCommand(cleanZipCommand)
}

fun runCommand(command: String) {
    val process = Runtime.getRuntime().exec(command)

    process.waitFor()
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
        downloadAndUnzip(url, binDir, binName, fullPath.absolutePath)
        logger.info("Successfully downloaded llm-ls")
    }

    return fullPath.absolutePath
}
