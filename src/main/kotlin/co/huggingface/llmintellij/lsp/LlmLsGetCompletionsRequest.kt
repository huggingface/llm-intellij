package co.huggingface.llmintellij.lsp

import com.intellij.platform.lsp.api.LspServer
import com.intellij.platform.lsp.api.requests.LspRequest
import java.util.concurrent.CompletableFuture

class LlmLsGetCompletionsRequest(lspServer: LspServer, private val params: CompletionParams): LspRequest<List<Completion>, List<Completion>>(lspServer) {
    override fun sendRequest(): CompletableFuture<List<Completion>> = (lspServer.lsp4jServer as LlmLsLanguageServer).getCompletions(params)
    override fun preprocessResponse(serverResponse: List<Completion>) = serverResponse
}
