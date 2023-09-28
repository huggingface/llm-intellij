package co.huggingface.llmintellij

import co.huggingface.llmintellij.lsp.LlmLsLanguageServer
import com.intellij.codeInsight.inline.completion.InlineCompletionElement
import com.intellij.codeInsight.inline.completion.InlineCompletionProvider
import com.intellij.codeInsight.inline.completion.InlineCompletionRequest
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.platform.lsp.api.LspServerManager


class LlmLsCompletionProvider: InlineCompletionProvider {
    private val logger = Logger.getInstance("inlineCompletion")

    override suspend fun getProposals(request: InlineCompletionRequest): List<InlineCompletionElement> {
        val project = request.editor.project
        if (project == null) {
            logger.error("could not find project")
            return emptyList()
        } else {
            val servers = LspServerManager.getInstance(project).getServersForProvider()
            logger.info(servers.toString())
            val server = servers.toList()[0]
            server.requestExecutor.sendRequestAsync()
            return listOf( InlineCompletionElement("toto") )
        }
    }

    override fun isEnabled(event: DocumentEvent): Boolean {
        return true
    }
}