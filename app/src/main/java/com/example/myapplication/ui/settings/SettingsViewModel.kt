package com.example.myapplication.ui.settings

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.settings.ApiKeyStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(
    private val apiKeyStore: ApiKeyStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(apiKeyInput = apiKeyStore.getApiKey().orEmpty())
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun onApiKeyChanged(newValue: String) {
        _uiState.value = _uiState.value.copy(apiKeyInput = newValue, isSaved = false)
    }

    fun saveApiKey() {
        val current = _uiState.value
        if (current.apiKeyInput.isBlank()) return
        apiKeyStore.setApiKey(current.apiKeyInput.trim())
        _uiState.value = current.copy(isSaved = true)
    }
}
