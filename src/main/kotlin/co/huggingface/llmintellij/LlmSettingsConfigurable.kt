package co.huggingface.llmintellij

import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent


class LlmSettingsConfigurable : Configurable {
    private var settingsComponent: LlmSettingsComponent? = null

    // A default constructor with no arguments is required because this implementation
    // is registered in an applicationConfigurable EP
    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String {
        return "LLM Settings"
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return settingsComponent?.preferredFocusedComponent
    }

    override fun createComponent(): JComponent? {
        settingsComponent = LlmSettingsComponent()
        return settingsComponent?.rootPanel
    }

    override fun isModified(): Boolean {
        val settings: LlmSettingsState = LlmSettingsState.instance
//        var modified: Boolean = !settingsComponent?.userNameText.equalVjs(settings.userId)
//        modified = modified or (settingsComponent?.ideaUserStatus !== settings.ideaStatus)
        return false
    }

    override fun apply() {
        val settings: LlmSettingsState = LlmSettingsState.instance
//        settings.userId = settingsComponent?.userNameText ?: ""
//        settings.ideaStatus = settingsComponent?.ideaUserStatus ?: false
    }

    override fun reset() {
        val settings: LlmSettingsState = LlmSettingsState.instance
//        settingsComponent?.userNameText = settings.userId
//        settingsComponent?.ideaUserStatus = settings.ideaStatus
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }
}