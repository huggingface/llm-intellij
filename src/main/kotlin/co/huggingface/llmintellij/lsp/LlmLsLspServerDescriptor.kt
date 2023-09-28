package co.huggingface.llmintellij.lsp

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor
import org.eclipse.lsp4j.services.LanguageServer


class LlmLsLspServerDescriptor(project: Project) : ProjectWideLspServerDescriptor(project, "LlmLs") {
    override fun isSupportedFile(file: VirtualFile) = true

    override fun createCommandLine(): GeneralCommandLine {
        val binaryPath = "/Users/mc/Documents/work/extensions/llm-ls/target/release/llm-ls"

        return GeneralCommandLine().apply {
            exePath = binaryPath
            withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE)
            withCharset(Charsets.UTF_8)
            withEnvironment("LLM_LOG_LEVEL", "info")
            addParameter("--stdio")
        }
    }

    override val lsp4jServerClass: Class<out LanguageServer>
        get() = LlmLsLanguageServer::class.java

    override val lspGoToDefinitionSupport = false

    override val lspCompletionSupport = null

    override val lspDiagnosticsSupport = null

    override val lspCodeActionsSupport = null

    override val lspCommandsSupport = null
}