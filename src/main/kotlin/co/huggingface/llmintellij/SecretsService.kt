package co.huggingface.llmintellij

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager

@Service(Service.Level.PROJECT)
class SecretsService(private val project: Project) {
    fun saveSecretSetting(secretValue: String) {
        // Store the secret value securely
        val credentialAttributes = CredentialAttributes(
            "SecretsService"
        )

        val credentials = Credentials("", secretValue)
        PasswordSafe.instance.set(credentialAttributes, credentials)
    }

    fun getSecretSetting(): String? {
        val credentialAttributes = CredentialAttributes(
            "SecretsService"
        )
        return PasswordSafe.instance.getPassword(credentialAttributes)
    }
    companion object {
        val instance: SecretsService
            get() = ProjectManager.getInstance().defaultProject.getService(SecretsService::class.java)
    }
}