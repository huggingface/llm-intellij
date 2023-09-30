package co.huggingface.llmintellij.lsp

import co.huggingface.llmintellij.FimParams
import co.huggingface.llmintellij.QueryParams
import co.huggingface.llmintellij.TokenizerConfig
import org.eclipse.lsp4j.TextDocumentIdentifier
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest
import org.eclipse.lsp4j.jsonrpc.services.JsonSegment
import org.eclipse.lsp4j.services.LanguageServer
import java.util.concurrent.CompletableFuture

data class Position(
    val line: Int,
    val character: Int
)
class CompletionParams(
    val textDocument: TextDocumentIdentifier,
    val position: Position,
    val request_params: QueryParams,
    val ide: String = "jetbrains",
    val fim: FimParams,
    val api_token: String?,
    val model: String,
    val tokens_to_clear: List<String>,
    val tokenizer_config: TokenizerConfig?,
    val context_window: UInt,
    val tls_skip_verify_insecure: Boolean = false,
)

@JsonSegment("llm-ls")
public interface LlmLsLanguageServer: LanguageServer {
    @JsonRequest
    fun getCompletions(params: CompletionParams): CompletableFuture<List<Completion>>;
}