package co.huggingface.llmintellij

import co.huggingface.llmintellij.lsp.CompletionParams
import co.huggingface.llmintellij.lsp.LlmLsGetCompletionsRequest
import co.huggingface.llmintellij.lsp.LlmLsServerSupportProvider
import co.huggingface.llmintellij.lsp.Position
import com.intellij.codeInsight.inline.completion.InlineCompletionElement
import com.intellij.codeInsight.inline.completion.InlineCompletionEvent
import com.intellij.codeInsight.inline.completion.InlineCompletionProvider
import com.intellij.codeInsight.inline.completion.InlineCompletionRequest
import com.intellij.openapi.diagnostic.Logger
import com.intellij.platform.lsp.api.LspServerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

class LlmLsCompletionProvider: InlineCompletionProvider {
    private val logger = Logger.getInstance("inlineCompletion")

    override suspend fun getProposals(request: InlineCompletionRequest): Flow<InlineCompletionElement> =
        channelFlow {
            val project = request.editor.project
            if (project == null) {
                logger.error("could not find project")
            } else {
                val settings = LlmSettingsState.instance
                val secrets = SecretsService.instance
                val lspServer = LspServerManager.getInstance(project).getServersForProvider(LlmLsServerSupportProvider::class.java).firstOrNull()
                if (lspServer != null) {
                    val textDocument = lspServer.requestExecutor.getDocumentIdentifier(request.file.virtualFile)
                    val caretPosition = request.editor.caretModel.offset
                    val line = request.document.getLineNumber(caretPosition)
                    val column = caretPosition - request.document.getLineStartOffset(line)
                    val position = Position(line, column)
                    val queryParams = settings.queryParams
                    val fimParams = settings.fim
                    val tokenizerConfig = settings.tokenizer
                    val params = CompletionParams(textDocument, position, request_params = queryParams, fim = fimParams, api_token = secrets.getSecretSetting(), model = settings.model, tokens_to_clear = settings.tokensToClear, tokenizer_config = tokenizerConfig, context_window = settings.contextWindow)
                    lspServer.requestExecutor.sendRequestAsync(LlmLsGetCompletionsRequest(lspServer, params)) { response ->
                        CoroutineScope(Dispatchers.Default).launch {
                            if (response != null) {
                                for (completion in response.completions) {
                                    send(InlineCompletionElement(completion.generated_text))
                                }
                            }
                        }
                    }
                }
            }
            awaitClose()
        }

    override fun isEnabled(event: InlineCompletionEvent): Boolean {
        val settings = LlmSettingsState.instance
        return settings.ghostTextEnabled
    }
    
    override fun isEnabled(event: DocumentEvent): Boolean {
        val settings = LlmSettingsState.instance
        return settings.ghostTextEnabled
    }
}
