package com.example.myapplication.ui.settings

import com.example.myapplication.data.remote.AiProvider
import com.example.myapplication.data.settings.AiSettingsStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

private class FakeAiSettingsStore(
    private var selectedProvider: AiProvider = AiProvider.CLAUDE,
    private val keys: MutableMap<AiProvider, String> = mutableMapOf()
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
}

class SettingsViewModelTest {

    @Test
    fun `initial state reports key registration without exposing stored key`() {
        val store = FakeAiSettingsStore(
            AiProvider.CLAUDE,
            mutableMapOf(AiProvider.CLAUDE to "secret")
        )
        val viewModel = SettingsViewModel(store)

        assertEquals("", viewModel.uiState.value.apiKeyInput)
        assertTrue(viewModel.uiState.value.hasStoredKey)
    }

    @Test
    fun `changing provider clears plaintext input and refreshes registration`() {
        val store = FakeAiSettingsStore(
            AiProvider.CLAUDE,
            mutableMapOf(AiProvider.OPENAI to "openai-key")
        )
        val viewModel = SettingsViewModel(store)

        viewModel.onApiKeyChanged("unsaved secret")
        viewModel.selectProvider(AiProvider.OPENAI)

        assertEquals(AiProvider.OPENAI, viewModel.uiState.value.selectedProvider)
        assertEquals("", viewModel.uiState.value.apiKeyInput)
        assertTrue(viewModel.uiState.value.hasStoredKey)
    }

    @Test
    fun `save trims key only for selected provider and clears input`() {
        val store = FakeAiSettingsStore(AiProvider.OPENAI)
        val viewModel = SettingsViewModel(store)
        viewModel.onApiKeyChanged("  openai-key  ")

        viewModel.saveApiKey()

        assertEquals("openai-key", store.getApiKey(AiProvider.OPENAI))
        assertNull(store.getApiKey(AiProvider.CLAUDE))
        assertEquals("", viewModel.uiState.value.apiKeyInput)
        assertEquals(AiProvider.OPENAI, viewModel.uiState.value.savedProvider)
    }

    @Test
    fun `clear removes only selected provider key`() {
        val store = FakeAiSettingsStore(
            AiProvider.OPENAI,
            mutableMapOf(
                AiProvider.CLAUDE to "claude-key",
                AiProvider.OPENAI to "openai-key"
            )
        )
        val viewModel = SettingsViewModel(store)

        viewModel.clearApiKey()

        assertEquals("claude-key", store.getApiKey(AiProvider.CLAUDE))
        assertNull(store.getApiKey(AiProvider.OPENAI))
        assertFalse(viewModel.uiState.value.hasStoredKey)
    }
}
