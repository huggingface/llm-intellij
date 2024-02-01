package co.huggingface.llmintellij

import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent
import kotlin.time.DurationUnit
import kotlin.time.toDuration


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
        var modified: Boolean = settingsComponent?.isGhostTextEnabled() != settings.ghostTextEnabled
        modified = modified or (settingsComponent?.getModelIdOrEndpoint() != settings.model)
        modified = modified or (settingsComponent?.getTokensToClear() != settings.tokensToClear)
        modified = modified or (settingsComponent?.getMaxNewTokens() != settings.queryParams.max_new_tokens)
        modified = modified or (settingsComponent?.getTemperature() != settings.queryParams.temperature)
        modified = modified or (settingsComponent?.getTopP() != settings.queryParams.top_p)
        modified = modified or (settingsComponent?.getStopTokens() != settings.queryParams.stop_tokens)
        modified = modified or (settingsComponent?.isFimEnabled() != settings.fim.enabled)
        modified = modified or (settingsComponent?.getFimPrefix() != settings.fim.prefix)
        modified = modified or (settingsComponent?.getFimMiddle() != settings.fim.middle)
        modified = modified or (settingsComponent?.getFimSuffix() != settings.fim.suffix)
        modified = modified or (settingsComponent?.isTlsSkipVerifyInsecureEnabled() != settings.tlsSkipVerifyInsecure)
        modified = modified or (settingsComponent?.getDebounceDelay() != settings.debounceDelay)
        modified = modified or (settingsComponent?.getLspBinaryPath() != settings.lsp.binaryPath)
        modified = modified or (settingsComponent?.getLspVersion() != settings.lsp.version)
        modified = modified or (settingsComponent?.getLspLogLevel() != settings.lsp.logLevel)
        modified = modified or (settingsComponent?.getTokenizerConfig() != settings.tokenizer)
        modified = modified or (settingsComponent?.getContextWindow() != settings.contextWindow)
        return modified
    }

    override fun apply() {
        val settings: LlmSettingsState = LlmSettingsState.instance
        settings.ghostTextEnabled = settingsComponent?.isGhostTextEnabled() ?: false
        settings.model = settingsComponent?.getModelIdOrEndpoint() ?: ""
        settings.tokensToClear = settingsComponent?.getTokensToClear() ?: emptyList()
        settings.queryParams.max_new_tokens = settingsComponent?.getMaxNewTokens() ?: 0u
        settings.queryParams.temperature = settingsComponent?.getTemperature() ?: 0f
        settings.queryParams.top_p = settingsComponent?.getTopP() ?: 0f
        settings.queryParams.stop_tokens = settingsComponent?.getStopTokens()
        settings.fim.enabled = settingsComponent?.isFimEnabled() ?: false
        settings.fim.prefix = settingsComponent?.getFimPrefix() ?: ""
        settings.fim.middle = settingsComponent?.getFimMiddle() ?: ""
        settings.fim.suffix = settingsComponent?.getFimSuffix() ?: ""
        settings.tlsSkipVerifyInsecure = settingsComponent?.isTlsSkipVerifyInsecureEnabled() ?: false
        settings.debounceDelay = settingsComponent?.getDebounceDelay() ?: 500.toDuration(DurationUnit.MILLISECONDS)
        settings.lsp.binaryPath = settingsComponent?.getLspBinaryPath()
        settings.lsp.version = settingsComponent?.getLspVersion() ?: ""
        settings.lsp.logLevel = settingsComponent?.getLspLogLevel() ?: ""
        settings.tokenizer = settingsComponent?.getTokenizerConfig()
        settings.contextWindow = settingsComponent?.getContextWindow() ?: 0u
    }

    override fun reset() {
        val settings: LlmSettingsState = LlmSettingsState.instance
        settingsComponent?.setGhostTextStatus(settings.ghostTextEnabled)
        settingsComponent?.setModelIdOrEndpoint(settings.model)
        settingsComponent?.setTokensToClear(settings.tokensToClear)
        settingsComponent?.setMaxNewTokens(settings.queryParams.max_new_tokens)
        settingsComponent?.setTemperature(settings.queryParams.temperature)
        settingsComponent?.setTopP(settings.queryParams.top_p)
        settingsComponent?.setStopTokens(settings.queryParams.stop_tokens ?: emptyList())
        settingsComponent?.setFimStatus(settings.fim.enabled)
        settingsComponent?.setFimPrefix(settings.fim.prefix)
        settingsComponent?.setFimMiddle(settings.fim.middle)
        settingsComponent?.setFimSuffix(settings.fim.suffix)
        settingsComponent?.setTlsSkipVerifyInsecureStatus(settings.tlsSkipVerifyInsecure)
        settingsComponent?.setDebounceDelay(settings.debounceDelay)
        settingsComponent?.setLspBinaryPath(settings.lsp.binaryPath ?: "")
        settingsComponent?.setLspVersion(settings.lsp.version)
        settingsComponent?.setLspLogLevel(settings.lsp.logLevel)
        settingsComponent?.setTokenizerConfig(settings.tokenizer)
        settingsComponent?.setContextWindow(settings.contextWindow)
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }
}