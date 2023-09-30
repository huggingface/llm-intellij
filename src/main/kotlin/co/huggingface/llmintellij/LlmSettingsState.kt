package co.huggingface.llmintellij

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

class LspSettings {
    var binaryPath: String? = null
}

class FimParams(
    val enabled: Boolean = true,
    val prefix: String = "<fim_prefix>",
    val middle: String = "<fim_middle>",
    val suffix: String = "<fim_suffix>",
)

class QueryParams(
    val max_new_tokens: UInt = 60u,
    val temperature: Float = 0.2f,
    val do_sample: Boolean = temperature > 0.2,
    val top_p: Float = 0.95f,
    val stop_tokens: List<String>? = null,
)

sealed class TokenizerConfig {
    data class Local(val path: String) : TokenizerConfig()
    data class HuggingFace(val repository: String) : TokenizerConfig()
    data class Download(val url: String, val to: String) : TokenizerConfig()
}

@State(
    name = "co.huggingface.llmintellij.LlmSettingsState",
    storages = [Storage("LlmSettingsPlugin.xml")]
)
class LlmSettingsState: PersistentStateComponent<LlmSettingsState?> {
    var api_token = null
    var model: String = "bigcode/starcoderbase"
    var tokensToClear: List<String> = listOf("<|endoftext|>")
    var queryParams = QueryParams()
    var fim = FimParams()
    var tlsSkipVerifyInsecure = false
    var lsp = LspSettings()
    var tokenizer: TokenizerConfig? = null
    var context_window = 8192

    override fun getState(): LlmSettingsState? {
        return this
    }

    override fun loadState(state: LlmSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        val instance: LlmSettingsState
            get() = ApplicationManager.getApplication().getService(LlmSettingsState::class.java)
    }
}
