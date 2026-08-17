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

    fun onModelPathChanged(path: String) {
        _uiState.value = _uiState.value.copy(modelPathInput = path, isModelSaved = false)
    }

    fun saveModelPath() {
        val state = _uiState.value
        val path = state.modelPathInput.trim()
        if (path.isNotBlank()) {
            settingsStore.setModelPath(path)
            _uiState.value = state.copy(
                storedModelPath = path,
                isModelSaved = true
            )
        }
    }

    private fun stateFor(provider: AiProvider): SettingsUiState {
        val storedPath = settingsStore.getModelPath()
        return SettingsUiState(
            selectedProvider = provider,
            hasStoredKey = !settingsStore.getApiKey(provider).isNullOrBlank(),
            storedModelPath = storedPath,
            modelPathInput = storedPath.orEmpty(),
            isModelSaved = !storedPath.isNullOrBlank()
        )
    }
}
