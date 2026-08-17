package com.example.myapplication.ui.settings

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasImeAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.input.ImeAction
import com.example.myapplication.data.remote.AiProvider
import com.example.myapplication.data.settings.AiSettingsStore
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

private class FakeAiSettingsStore(
    private var selectedProvider: AiProvider = AiProvider.LOCAL_BANK,
    private val keys: MutableMap<AiProvider, String> = mutableMapOf(),
    private var modelPath: String? = null
) : AiSettingsStore {
    override fun getSelectedProvider(): AiProvider = selectedProvider

    override fun setSelectedProvider(provider: AiProvider) {
        selectedProvider = provider
    }

    override fun getApiKey(provider: AiProvider): String? = keys[provider]

    override fun setApiKey(provider: AiProvider, apiKey: String) {
        keys[provider] = apiKey
    }

    override fun clearApiKey(provider: AiProvider) {
        keys.remove(provider)
    }

    override fun getModelPath(): String? = modelPath

    override fun setModelPath(path: String) {
        modelPath = path
    }
}

class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun localBank_showsZeroApiKeyDescription() {
        composeTestRule.setContent {
            SettingsScreen(viewModel = SettingsViewModel(FakeAiSettingsStore(AiProvider.LOCAL_BANK)))
        }

        composeTestRule.onNodeWithText("✨ 제로 API 키 & 제로 레이턴시").assertExists()
    }

    @Test
    fun apiKeyInput_hasSingleLineImeAction() {
        composeTestRule.setContent {
            SettingsScreen(viewModel = SettingsViewModel(FakeAiSettingsStore(AiProvider.CLAUDE)))
        }

        composeTestRule.onNodeWithTag("apiKeyInput").assert(hasImeAction(ImeAction.Done))
    }

    @Test
    fun changingProvider_clearsInputAndShowsRegisteredState() {
        val store = FakeAiSettingsStore(
            AiProvider.CLAUDE,
            mutableMapOf(AiProvider.OPENAI to "secret")
        )
        val viewModel = SettingsViewModel(store)
        composeTestRule.setContent {
            SettingsScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag("apiKeyInput").performTextInput("plaintext")
        composeTestRule.onNodeWithTag("providerOpenAi").performClick()

        composeTestRule.onNodeWithTag("apiKeyInput").assertTextEquals("")
        composeTestRule.onNodeWithText("OpenAI API 키가 등록되어 있습니다.").assertExists()
    }

    @Test
    fun savingOpenAiKey_showsProviderSpecificConfirmation() {
        val viewModel = SettingsViewModel(FakeAiSettingsStore(AiProvider.CLAUDE))
        composeTestRule.setContent {
            SettingsScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag("providerOpenAi").performClick()
        composeTestRule.onNodeWithTag("apiKeyInput").performTextInput("openai-key")
        composeTestRule.onNodeWithText("저장").performClick()

        composeTestRule.onNodeWithText("OpenAI API 키를 저장했습니다.").assertExists()
    }

    @Test
    fun deletingOpenAiKey_leavesClaudeKeyIntact() {
        val store = FakeAiSettingsStore(
            AiProvider.OPENAI,
            mutableMapOf(
                AiProvider.CLAUDE to "claude-key",
                AiProvider.OPENAI to "openai-key"
            )
        )
        val viewModel = SettingsViewModel(store)
        composeTestRule.setContent {
            SettingsScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag("clearApiKey").performClick()

        composeTestRule.onNodeWithText("OpenAI API 키가 등록되어 있지 않습니다.").assertExists()
        assertEquals("claude-key", store.getApiKey(AiProvider.CLAUDE))
    }
}
