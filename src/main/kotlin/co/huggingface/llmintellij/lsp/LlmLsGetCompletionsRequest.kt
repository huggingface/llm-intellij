package co.huggingface.llmintellij.lsp

import com.intellij.platform.lsp.api.LspServer
import com.intellij.platform.lsp.api.requests.LspRequest
import java.util.concurrent.CompletableFuture

class LlmLsGetCompletionsRequest(lspServer: LspServer, private val params: CompletionParams): LspRequest<CompletionResponse, CompletionResponse>(lspServer) {
    override fun sendRequest(): CompletableFuture<CompletionResponse> = (lspServer.lsp4jServer as LlmLsLanguageServer).getCompletions(params)
    override fun preprocessResponse(serverResponse: CompletionResponse) = serverResponse
}
