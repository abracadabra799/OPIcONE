package com.example.myapplication.ui.settings

import com.example.myapplication.data.settings.ApiKeyStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

private class FakeApiKeyStore(private var key: String? = null) : ApiKeyStore {
    override fun getApiKey(): String? = key
    override fun setApiKey(apiKey: String) { key = apiKey }
    override fun clearApiKey() { key = null }
}

class SettingsViewModelTest {

    @Test
    fun `initial state loads existing key from the store`() {
        val viewModel = SettingsViewModel(FakeApiKeyStore(key = "sk-existing"))

        assertEquals("sk-existing", viewModel.uiState.value.apiKeyInput)
    }

    @Test
    fun `onApiKeyChanged updates input and clears saved flag`() {
        val viewModel = SettingsViewModel(FakeApiKeyStore())

        viewModel.onApiKeyChanged("sk-new-key")

        assertEquals("sk-new-key", viewModel.uiState.value.apiKeyInput)
        assertFalse(viewModel.uiState.value.isSaved)
    }

    @Test
    fun `saveApiKey persists the trimmed key and sets saved flag`() {
        val store = FakeApiKeyStore()
        val viewModel = SettingsViewModel(store)
        viewModel.onApiKeyChanged("  sk-new-key  ")

        viewModel.saveApiKey()

        assertEquals("sk-new-key", store.getApiKey())
        assertTrue(viewModel.uiState.value.isSaved)
    }

    @Test
    fun `saveApiKey with blank input does not persist`() {
        val store = FakeApiKeyStore(key = "sk-existing")
        val viewModel = SettingsViewModel(store)
        viewModel.onApiKeyChanged("   ")

        viewModel.saveApiKey()

        assertEquals("sk-existing", store.getApiKey())
        assertFalse(viewModel.uiState.value.isSaved)
    }
}
