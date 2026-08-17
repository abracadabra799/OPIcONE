package com.example.myapplication.ui.settings

import com.example.myapplication.data.remote.AiProvider

data class SettingsUiState(
    val selectedProvider: AiProvider = AiProvider.LOCAL_BANK,
    val apiKeyInput: String = "",
    val hasStoredKey: Boolean = false,
    val savedProvider: AiProvider? = null,
    val modelPathInput: String = "",
    val storedModelPath: String? = null,
    val isModelSaved: Boolean = false
)
