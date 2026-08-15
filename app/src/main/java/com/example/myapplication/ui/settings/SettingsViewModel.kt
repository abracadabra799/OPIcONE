package com.example.myapplication.ui.settings

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.remote.AiProvider
import com.example.myapplication.data.settings.AiSettingsStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(
    private val settingsStore: AiSettingsStore
) : ViewModel() {

    private val initialProvider = settingsStore.getSelectedProvider()
    private val _uiState = MutableStateFlow(stateFor(initialProvider))
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun selectProvider(provider: AiProvider) {
        if (provider == _uiState.value.selectedProvider) return
        settingsStore.setSelectedProvider(provider)
        _uiState.value = stateFor(provider)
    }

    fun onApiKeyChanged(value: String) {
        _uiState.value = _uiState.value.copy(apiKeyInput = value, savedProvider = null)
    }

    fun saveApiKey() {
        val state = _uiState.value
        val key = state.apiKeyInput.trim().takeIf(String::isNotBlank) ?: return
        settingsStore.setApiKey(state.selectedProvider, key)
        _uiState.value = state.copy(
            apiKeyInput = "",
            hasStoredKey = true,
            savedProvider = state.selectedProvider
        )
    }

    fun clearApiKey() {
        val provider = _uiState.value.selectedProvider
        settingsStore.clearApiKey(provider)
        _uiState.value = stateFor(provider)
    }

    private fun stateFor(provider: AiProvider) = SettingsUiState(
        selectedProvider = provider,
        hasStoredKey = !settingsStore.getApiKey(provider).isNullOrBlank()
    )
}
