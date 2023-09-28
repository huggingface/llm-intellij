package co.huggingface.llmintellij

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
class ProjectService(val project: Project) {
}