package co.huggingface.llmintellij

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.TitledSeparator
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import com.intellij.ui.components.panels.VerticalLayout
import java.awt.FlowLayout
import java.awt.event.ItemEvent
import javax.swing.BoxLayout
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class LlmSettingsComponent {
    val rootPanel: JPanel = JPanel()
    private val apiTokenLabel: JBLabel
    private val apiToken: JBPasswordField
    private val modelLabel: JBLabel
    private val model: JBTextField
    private val tokensToClearLabel: JBLabel
    private val tokensToClear: JBTextField
    private val maxNewTokensLabel: JBLabel
    private val maxNewTokens: JBTextField
    private val temperatureLabel: JBLabel
    private val temperature: JBTextField
    private val topPLabel: JBLabel
    private val topP: JBTextField
    private val stopTokensLabel: JBLabel
    private val stopTokens: JBTextField
    private val fim: JBCheckBox
    private val fimPrefixLabel: JBLabel
    private val fimPrefix: JBTextField
    private val fimMiddleLabel: JBLabel
    private val fimMiddle: JBTextField
    private val fimSuffixLabel: JBLabel
    private val fimSuffix: JBTextField
    private val tlsSkipVerifyInsecure: JBCheckBox
    private val lspBinaryPath: TextFieldWithBrowseButton
    private val tokenizerConfig: JComboBox<String>
    private val tokenizerConfigLocalPathLabel: JBLabel
    private val tokenizerConfigLocalPath: TextFieldWithBrowseButton
    private val tokenizerConfigHuggingFaceRepositoryLabel: JBLabel
    private val tokenizerConfigHuggingFaceRepository: JBTextField
    private val tokenizerConfigDownloadUrlLabel: JBLabel
    private val tokenizerConfigDownloadUrl: JBTextField
    private val tokenizerConfigDownloadToLabel: JBLabel
    private val tokenizerConfigDownloadTo: JBTextField
    private val contextWindowLabel: JBLabel
    private val contextWindow: JBTextField
    private val enableGhostText: JBCheckBox

    init {
        // Used for all text fields with browse button
        val descriptor = FileChooserDescriptor(true, false, false, false, false, false)

        rootPanel.layout = BoxLayout(rootPanel, BoxLayout.Y_AXIS)

        val enableGhostTextPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        enableGhostText = JBCheckBox("Enable ghost text", true)
        enableGhostTextPanel.add(enableGhostText)
        rootPanel.add(enableGhostTextPanel)

        val modelSectionPanel = createSectionPanel("Model", rootPanel)

        apiTokenLabel = JBLabel("API token")
        apiToken = JBPasswordField()
        apiToken.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) {
                val apiTokenValue = String(apiToken.password)
                SecretsService.instance.saveSecretSetting(apiTokenValue)
            }

            override fun removeUpdate(e: DocumentEvent) {
                val apiTokenValue = String(apiToken.password)
                SecretsService.instance.saveSecretSetting(apiTokenValue)
            }

            override fun changedUpdate(e: DocumentEvent) {
                val apiTokenValue = String(apiToken.password)
                SecretsService.instance.saveSecretSetting(apiTokenValue)
            }
        })
        modelSectionPanel.add(apiTokenLabel)
        modelSectionPanel.add(apiToken)
        modelLabel = JBLabel("Model or endpoint url")
        model = JBTextField("bigcode/starcoder")
        modelSectionPanel.add(modelLabel)
        modelSectionPanel.add(model)
        tokensToClearLabel = JBLabel("Tokens to clear (Comma-separated List)")
        tokensToClear = JBTextField("<|endoftext|>")
        modelSectionPanel.add(tokensToClearLabel)
        modelSectionPanel.add(tokensToClear)
        tlsSkipVerifyInsecure = JBCheckBox("TLS skip verify insecure")
        modelSectionPanel.add(tlsSkipVerifyInsecure)

        val queryParamsSubsectionPanel = createSectionPanel("Query Params", modelSectionPanel)
        maxNewTokensLabel = JBLabel("Max new tokens")
        maxNewTokens = JBTextField("60")
        queryParamsSubsectionPanel.add(maxNewTokensLabel)
        queryParamsSubsectionPanel.add(maxNewTokens)
        temperatureLabel = JBLabel("Temperature")
        temperature = JBTextField("0.2")
        queryParamsSubsectionPanel.add(temperatureLabel)
        queryParamsSubsectionPanel.add(temperature)
        topPLabel = JBLabel("Top P")
        topP = JBTextField("0.95")
        queryParamsSubsectionPanel.add(topPLabel)
        queryParamsSubsectionPanel.add(topP)
        stopTokensLabel = JBLabel("Stop tokens (Comma-separated List)")
        stopTokens = JBTextField()
        queryParamsSubsectionPanel.add(stopTokensLabel)
        queryParamsSubsectionPanel.add(stopTokens)

        val promptSectionPanel = createSectionPanel("Prompt", rootPanel)
        contextWindowLabel = JBLabel("Context window")
        contextWindow = JBTextField("8192")
        promptSectionPanel.add(contextWindowLabel)
        promptSectionPanel.add(contextWindow)

        val fillInTheMiddleSubsectionPanel = createSectionPanel("Fill in the Middle", promptSectionPanel)
        fim = JBCheckBox("Enabled", true)
        fillInTheMiddleSubsectionPanel.add(fim)
        fimPrefixLabel = JBLabel("Prefix")
        fimPrefix = JBTextField("<fim_prefix>")
        fillInTheMiddleSubsectionPanel.add(fimPrefixLabel)
        fillInTheMiddleSubsectionPanel.add(fimPrefix)
        fimMiddleLabel = JBLabel("Middle")
        fimMiddle = JBTextField("<fim_middle>")
        fillInTheMiddleSubsectionPanel.add(fimMiddleLabel)
        fillInTheMiddleSubsectionPanel.add(fimMiddle)
        fimSuffixLabel = JBLabel("Suffix")
        fimSuffix = JBTextField("<fim_suffix>")
        fillInTheMiddleSubsectionPanel.add(fimSuffixLabel)
        fillInTheMiddleSubsectionPanel.add(fimSuffix)

        fim.addItemListener { event ->
            if (event.stateChange == ItemEvent.SELECTED) {
                fimPrefixLabel.isEnabled = true
                fimPrefix.isEnabled = true
                fimMiddleLabel.isEnabled = true
                fimMiddle.isEnabled = true
                fimSuffixLabel.isEnabled = true
                fimSuffix.isEnabled = true
            } else {
                fimPrefixLabel.isEnabled = false
                fimPrefix.isEnabled = false
                fimMiddleLabel.isEnabled = false
                fimMiddle.isEnabled = false
                fimSuffixLabel.isEnabled = false
                fimSuffix.isEnabled = false
            }
        }
        val tokenizerSubsectionPanel = createSectionPanel("Tokenizer", promptSectionPanel)
        val tokenizerOptions = arrayOf("Hugging Face", "Local", "Download", "None")
        tokenizerConfig = JComboBox(tokenizerOptions)
        tokenizerSubsectionPanel.add(tokenizerConfig)

        tokenizerConfigLocalPathLabel = JBLabel("Path")
        tokenizerConfigLocalPath = TextFieldWithBrowseButton()
        tokenizerConfigLocalPath.addBrowseFolderListener("Select Path", null, null, descriptor)
        tokenizerConfigLocalPathLabel.isVisible = false
        tokenizerConfigLocalPath.isVisible = false
        tokenizerSubsectionPanel.add(tokenizerConfigLocalPathLabel)
        tokenizerSubsectionPanel.add(tokenizerConfigLocalPath)

        tokenizerConfigHuggingFaceRepositoryLabel = JBLabel("Repository")
        tokenizerConfigHuggingFaceRepository = JBTextField("bigcode/starcoder")
        tokenizerSubsectionPanel.add(tokenizerConfigHuggingFaceRepositoryLabel)
        tokenizerSubsectionPanel.add(tokenizerConfigHuggingFaceRepository)

        tokenizerConfigDownloadUrlLabel = JBLabel("URL")
        tokenizerConfigDownloadUrl = JBTextField()
        tokenizerConfigDownloadUrlLabel.isVisible = false
        tokenizerConfigDownloadUrl.isVisible = false
        tokenizerSubsectionPanel.add(tokenizerConfigDownloadUrlLabel)
        tokenizerSubsectionPanel.add(tokenizerConfigDownloadUrl)
        tokenizerConfigDownloadToLabel = JBLabel("To")
        tokenizerConfigDownloadTo = JBTextField()
        tokenizerConfigDownloadToLabel.isVisible = false
        tokenizerConfigDownloadTo.isVisible = false
        tokenizerSubsectionPanel.add(tokenizerConfigDownloadToLabel)
        tokenizerSubsectionPanel.add(tokenizerConfigDownloadTo)

        tokenizerConfig.addActionListener { _ ->
            val selectedValue = tokenizerConfig.selectedItem as String
            tokenizerConfigLocalPathLabel.isVisible = selectedValue == "Local"
            tokenizerConfigLocalPath.isVisible = selectedValue == "Local"
            tokenizerConfigHuggingFaceRepositoryLabel.isVisible = selectedValue == "Hugging Face"
            tokenizerConfigHuggingFaceRepository.isVisible = selectedValue == "Hugging Face"
            tokenizerConfigDownloadUrlLabel.isVisible = selectedValue == "Download"
            tokenizerConfigDownloadUrl.isVisible = selectedValue == "Download"
            tokenizerConfigDownloadToLabel.isVisible = selectedValue == "Download"
            tokenizerConfigDownloadTo.isVisible = selectedValue == "Download"
        }

        val llmLsSubsectionPanel = createSectionPanel("llm-ls", rootPanel)
        lspBinaryPath = TextFieldWithBrowseButton()
        lspBinaryPath.addBrowseFolderListener("Select Path", null, null, descriptor)
        val lspBinaryPathLabel = JBLabel("Binary path")
        llmLsSubsectionPanel.add(lspBinaryPathLabel)
        llmLsSubsectionPanel.add(lspBinaryPath)

    }

    val preferredFocusedComponent: JComponent
        get() = model

    public fun isGhostTextEnabled(): Boolean {
        return enableGhostText.isSelected
    }

    public fun setGhostTextStatus(enabled: Boolean) {
        enableGhostText.setSelected(enabled)
    }

    public fun getModelIdOrEndpoint(): String {
        return model.text
    }

    public fun setModelIdOrEndpoint(value: String) {
        model.text = value
    }

    public fun getTokensToClear(): List<String>? {
        val tokensStr = tokensToClear.text
        return if (tokensStr == "") {
            null
        }else {
            tokensStr.split(",")
        }
    }

    public fun setTokensToClear(tokens: List<String>?) {
        if (tokens == null) {
            tokensToClear.text = null
        } else {
            tokensToClear.text = tokens.joinToString(",")
        }
    }

    public fun getMaxNewTokens(): UInt {
        return maxNewTokens.text.toUInt()
    }

    public fun setMaxNewTokens(value: UInt) {
        maxNewTokens.text = value.toString()
    }

    public fun getTemperature(): Float {
        return temperature.text.toFloat()
    }

    public fun setTemperature(value: Float) {
        temperature.text = value.toString()
    }

    public fun getTopP(): Float {
        return topP.text.toFloat()
    }

    public fun setTopP(value: Float) {
        topP.text = value.toString()
    }

    public fun getStopTokens(): List<String>? {
        val stopTokensStr = stopTokens.text
        return if (stopTokensStr == "") {
            null
        } else {
            stopTokensStr.split(",")
        }
    }

    public fun setStopTokens(tokens: List<String>?) {
        if (tokens == null) {
            stopTokens.text = ""
        } else {
            stopTokens.text = tokens.joinToString(",")
        }
    }

    public fun isFimEnabled(): Boolean {
        return fim.isSelected
    }

    public fun setFimStatus(enabled: Boolean) {
        fim.setSelected(enabled)
    }

    public fun getFimPrefix(): String {
        return fimPrefix.text
    }

    public fun setFimPrefix(value: String) {
        fimPrefix.text = value
    }

    public fun getFimMiddle(): String {
        return fimMiddle.text
    }

    public fun setFimMiddle(value: String) {
        fimMiddle.text = value
    }

    public fun getFimSuffix(): String {
        return fimSuffix.text
    }

    public fun setFimSuffix(value: String) {
        fimSuffix.text = value
    }

    public fun isTlsSkipVerifyInsecureEnabled(): Boolean {
        return tlsSkipVerifyInsecure.isSelected
    }

    public fun setTlsSkipVerifyInsecureStatus(enabled: Boolean) {
        tlsSkipVerifyInsecure.setSelected(enabled)
    }

    public fun getLspBinaryPath(): String? {
        val binaryPath = lspBinaryPath.text
        return if (binaryPath == "") {
            null
        } else {
            binaryPath
        }
    }

    public fun setLspBinaryPath(value: String) {
        lspBinaryPath.text = value
    }

    public fun getTokenizerConfig(): TokenizerConfig? {
        val type = tokenizerConfig.getItemAt(tokenizerConfig.selectedIndex)
        return when (type) {
            "Hugging Face" -> {
                TokenizerConfig.HuggingFace(tokenizerConfigHuggingFaceRepository.text)
            }
            "Local" -> {
                TokenizerConfig.Local(tokenizerConfigLocalPath.text)
            }
            "Download" -> {
                TokenizerConfig.Download(tokenizerConfigDownloadUrl.text, tokenizerConfigDownloadTo.text)
            }
            else -> {
                null
            }
        }
    }

    public fun setTokenizerConfig(value: TokenizerConfig?) {
        when (value) {
            is TokenizerConfig.HuggingFace -> {
                tokenizerConfig.selectedItem = "Hugging Face"
                tokenizerConfigHuggingFaceRepository.text = value.repository
            }

            is TokenizerConfig.Local -> {
                tokenizerConfig.selectedItem = "Local"
                tokenizerConfigLocalPath.text = value.path
            }

            is TokenizerConfig.Download -> {
                tokenizerConfig.selectedItem = "Download"
                tokenizerConfigDownloadUrl.text = value.url
                tokenizerConfigDownloadTo.text = value.to
            }
            null -> {
                tokenizerConfig.selectedItem = "None"
            }
        }
    }

    public fun getContextWindow(): UInt {
        return contextWindow.text.toUInt()
    }

    public fun setContextWindow(value: UInt) {
        contextWindow.text = value.toString()
    }

    private fun createSectionPanel(title: String, parentPanel: JPanel): JPanel {
        val panel = JPanel()
        panel.setLayout(VerticalLayout(5))
        val titledSeparator = TitledSeparator()
        titledSeparator.text = title
        panel.add(titledSeparator)
        parentPanel.add(panel)
        return panel
    }
}