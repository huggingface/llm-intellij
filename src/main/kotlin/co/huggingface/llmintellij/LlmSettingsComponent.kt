package co.huggingface.llmintellij

import com.intellij.ui.TitledSeparator
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.ui.components.panels.VerticalLayout
import java.awt.Component
import java.awt.event.ItemEvent
import javax.swing.BoxLayout
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JPanel

class LlmSettingsComponent {
    val rootPanel: JPanel = JPanel()
    private val apiTokenLabel: JBLabel
    private val apiToken: JBTextField
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
    // private val lspBinaryPath = JBComboBox or something like that
    private val tokenizerConfig: JComboBox<String>
    private val tokenizerConfigLocalPathLabel: JBLabel
    private val tokenizerConfigLocalPath: JBTextField
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

        rootPanel.layout = BoxLayout(rootPanel, BoxLayout.Y_AXIS)

        enableGhostText = JBCheckBox("Enable ghost text", true)
        enableGhostText.alignmentX = Component.LEFT_ALIGNMENT
        rootPanel.add(enableGhostText)

        val modelSectionPanel = createSectionPanel("Model", rootPanel)

        apiTokenLabel = JBLabel("API token")
        apiToken = JBTextField()
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
        maxNewTokens = JBTextField(60)
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
        contextWindow = JBTextField(8192)
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
                fimPrefixLabel.isVisible = true
                fimPrefix.isVisible = true
                fimMiddleLabel.isVisible = true
                fimMiddle.isVisible = true
                fimSuffixLabel.isVisible = true
                fimSuffix.isVisible = true
            } else {
                fimPrefix.text = ""
                fimPrefixLabel.isVisible = false
                fimPrefix.isVisible = false
                fimMiddle.text = ""
                fimMiddleLabel.isVisible = false
                fimMiddle.isVisible = false
                fimSuffix.text = ""
                fimSuffixLabel.isVisible = false
                fimSuffix.isVisible = false
            }
        }
        val tokenizerSubsectionPanel = createSectionPanel("Tokenizer", promptSectionPanel)
        val tokenizerOptions = arrayOf("Hugging Face", "Local", "Download")
        tokenizerConfig = JComboBox(tokenizerOptions)
        tokenizerSubsectionPanel.add(tokenizerConfig)

        tokenizerConfigLocalPathLabel = JBLabel("Path")
        tokenizerConfigLocalPath = JBTextField()
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

//        rootPanel = FormBuilder.createFormBuilder()
//            .addLabeledComponent("API token: ", apiToken, 1, false)
//            .addVerticalGap(1)
//            .addLabeledComponent("Hugging Face model ID or endpoint URL: ", model, 1, false)
//            .addVerticalGap(1)
//            .addLabeledComponent("Comma separated list of tokens to remove from model's response: ", tokensToClear, 1, false)
//            .addLabeledComponent("Max number of tokens to generate: ", maxNewTokens, 1, false)
//            .addLabeledComponent("Temperature: ", temperature, 1, false)
//            .addLabeledComponent("Top p: ", topP, 1, false)
//            .addLabeledComponent("Comma separated list of stop tokens: ", stopTokens, 1, false)
//            .addVerticalGap(1)
//            .addComponent(fim, 1)
//            .addLabeledComponent("FIM prefix: ", fimPrefix, 1, false)
//            .addLabeledComponent("FIM middle: ", fimMiddle, 1, false)
//            .addLabeledComponent("FIM suffix: ", fimSuffix, 1, false)
//            .addLabeledComponent("TLS skip verify insecure: ", tlsSkipVerifyInsecure, 1, false)
//            .addVerticalGap(1)
//            .addLabeledComponent("Context window length: ", contextWindow, 1, false)
//            .addVerticalGap(1)
//            .addLabeledComponent("Enable ghost-text suggestions: ", enableGhostText, 1, false)
//            .addVerticalGap(1)
//            .addComponent(tokenizerConfigLocal)
//            .addComponent(tokenizerConfigHuggingFace)
//            .addComponent(tokenizerConfigDownload)
//            .addComponentFillVertically(JPanel(), 0)
//            .panel
    }

    val preferredFocusedComponent: JComponent
        get() = model

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