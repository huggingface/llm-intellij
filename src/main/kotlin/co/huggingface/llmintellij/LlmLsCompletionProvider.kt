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
            val lspServer = LspServerManager.getInstance(project).getServersForProvider(LlmLsServerSupportProvider::class.java).firstOrNull() ?: return emptyList()
            val textDocument = lspServer.requestExecutor.getDocumentIdentifier(request.file.virtualFile)
            val caretPosition = request.startOffset
            val line = request.document.getLineNumber(request.startOffset)
            val position = Position(line, caretPosition)
            val queryParams = QueryParams(60u, 0.2f, true, 0.95f, listOf("<|endoftext|>"))
            val fimParams = FimParams(true, "<fim_prefix>", "<fim_middle>", "<fim_suffix>")
            val tokenizerConfig = TokenizerConfig.Local(path = "/Users/mc/.cache/llm_ls/bigcode/starcoder/tokenizer.json")
            val params = CompletionParams(textDocument, position, request_params = queryParams, fim = fimParams, api_token = "hf_dummy", model = "http://localhost:4242", tokens_to_clear = listOf("<|endoftext|>"), tokenizer_config = tokenizerConfig, context_window = 8192u)
            val completions = lspServer.requestExecutor.sendRequestSync(LlmLsGetCompletionsRequest(lspServer, params)) ?: return emptyList()
            completions.map { InlineCompletionElement(it.generated_text) }
        }
    }

    override fun isEnabled(event: DocumentEvent): Boolean {
        return true
    }
}