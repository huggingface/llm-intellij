package co.huggingface.llmintellij

import co.huggingface.llmintellij.lsp.*
import com.intellij.codeInsight.inline.completion.InlineCompletionElement
import com.intellij.codeInsight.inline.completion.InlineCompletionProvider
import com.intellij.codeInsight.inline.completion.InlineCompletionRequest
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.platform.lsp.api.LspServerManager


class LlmLsCompletionProvider: InlineCompletionProvider {
    private val logger = Logger.getInstance("inlineCompletion")

    override suspend fun getProposals(request: InlineCompletionRequest): List<InlineCompletionElement> {
        logger.info("getProposals")
        val project = request.editor.project
        return if (project == null) {
            logger.error("could not find project")
            emptyList()
        } else {
            val settings = LlmSettingsState.instance
            val secrets = SecretsService.instance
            val lspServer = LspServerManager.getInstance(project).getServersForProvider(LlmLsServerSupportProvider::class.java).firstOrNull() ?: return emptyList()
            val textDocument = lspServer.requestExecutor.getDocumentIdentifier(request.file.virtualFile)
            val caretPosition = request.startOffset
            val line = request.document.getLineNumber(request.startOffset)
            val position = Position(line, caretPosition)
            val queryParams = settings.queryParams
            val fimParams = settings.fim
            val tokenizerConfig = settings.tokenizer
            val params = CompletionParams(textDocument, position, request_params = queryParams, fim = fimParams, api_token = secrets.getSecretSetting(), model = settings.model, tokens_to_clear = settings.tokensToClear, tokenizer_config = tokenizerConfig, context_window = settings.contextWindow)
            val completions = lspServer.requestExecutor.sendRequestSync(LlmLsGetCompletionsRequest(lspServer, params)) ?: return emptyList()
            completions.map { InlineCompletionElement(it.generated_text) }
        }
    }

    override fun isEnabled(event: DocumentEvent): Boolean {
        return true
    }
}