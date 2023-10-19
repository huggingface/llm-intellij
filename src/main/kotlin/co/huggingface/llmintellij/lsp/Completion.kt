package co.huggingface.llmintellij.lsp

class Completion(val generated_text: String)

class CompletionResponse {
    val request_id: String = ""
    val completions: List<Completion> = emptyList()
}