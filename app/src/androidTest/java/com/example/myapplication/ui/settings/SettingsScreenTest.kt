package com.example.myapplication.ui.settings

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.myapplication.data.settings.ApiKeyStore
import org.junit.Rule
import org.junit.Test

private class FakeApiKeyStore(private var key: String? = null) : ApiKeyStore {
    override fun getApiKey(): String? = key
    override fun setApiKey(apiKey: String) { key = apiKey }
    override fun clearApiKey() { key = null }
}

class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun enteringAKeyAndTappingSave_showsConfirmation() {
        val viewModel = SettingsViewModel(FakeApiKeyStore())
        composeTestRule.setContent {
            SettingsScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag("apiKeyInput").performTextInput("sk-ant-test")
        composeTestRule.onNodeWithText("저장").performClick()

        composeTestRule.onNodeWithText("저장되었습니다.").assertExists()
    }
}
