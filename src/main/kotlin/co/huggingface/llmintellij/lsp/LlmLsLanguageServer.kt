package co.huggingface.llmintellij.lsp

import org.eclipse.lsp4j.jsonrpc.services.JsonSegment
import org.eclipse.lsp4j.services.LanguageServer
import java.util.concurrent.CompletableFuture

@JsonSegment("llm-ls")
public interface LlmLsLanguageServer: LanguageServer {
    fun getCompletions(): CompletableFuture<List<Completion>>;
}