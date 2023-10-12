package co.huggingface.llmintellij.lsp

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspServerSupportProvider

class LlmLsServerSupportProvider : LspServerSupportProvider {
    override fun fileOpened(project: Project, file: VirtualFile, serverStarter: LspServerSupportProvider.LspServerStarter) {
        serverStarter.ensureServerStarted(LlmLsLspServerDescriptor(project))
    }

//    override fun fileChanged(project: Project, file: VirtualFile, serverStarter: LspServerSupportProvider.LspServerStarter) {
//    }
}