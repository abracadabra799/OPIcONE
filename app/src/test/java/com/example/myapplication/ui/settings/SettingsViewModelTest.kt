package com.example.myapplication.ui.settings

import com.example.myapplication.data.remote.AiProvider
import com.example.myapplication.data.settings.AiSettingsStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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

class SettingsViewModelTest {

    @Test
    fun `initial state defaults to LOCAL_BANK and reports stored key state`() {
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
    fun `selecting current provider preserves unsaved input`() {
        val store = FakeAiSettingsStore(AiProvider.OPENAI)
        val viewModel = SettingsViewModel(store)
        viewModel.onApiKeyChanged("unsaved openai key")

        viewModel.selectProvider(AiProvider.OPENAI)

        assertEquals("unsaved openai key", viewModel.uiState.value.apiKeyInput)
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

    @Test
    fun `can save on-device model path`() {
        val store = FakeAiSettingsStore(AiProvider.ON_DEVICE_LLM)
        val viewModel = SettingsViewModel(store)

        viewModel.onModelPathChanged("/sdcard/Download/gemma-2b-it.bin")
        viewModel.saveModelPath()

        assertEquals("/sdcard/Download/gemma-2b-it.bin", store.getModelPath())
        assertTrue(viewModel.uiState.value.isModelSaved)
    }
}
